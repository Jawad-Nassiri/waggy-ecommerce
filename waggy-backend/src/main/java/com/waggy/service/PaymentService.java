package com.waggy.service;

import java.util.List;

import com.waggy.entity.Cart;
import com.waggy.entity.User;
import com.waggy.exception.CartNotFoundException;
import com.waggy.repository.CartRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.waggy.dto.payment.PaymentRequestDTO;
import com.waggy.dto.payment.PaymentResponseDTO;
import com.waggy.entity.Order;
import com.waggy.exception.OrderNotFoundException;
import com.waggy.repository.OrderRepository;

@Service
public class PaymentService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    public PaymentService(OrderRepository orderRepository, CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
    }

    public PaymentResponseDTO createCheckoutSession(PaymentRequestDTO dto) {

        Order order = orderRepository.findById(dto.orderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found !"));

//        this part takes all the items in the order and converts each one into a Stripe line item.
        List<SessionCreateParams.LineItem> lineItems = order.getOrderItems()
                .stream()
                .map(item -> SessionCreateParams.LineItem.builder()
                        .setQuantity(item.getQuantity().longValue())
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("eur")
                                        .setUnitAmount(item.getPrice().movePointRight(2).longValue())
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName(item.getProduct().getName())
                                                        .build()
                                        )
                                        .build()
                        )
                        .build()
                )
                .toList();

        // this part sets the payment details.
        // after payment → go to the success page.
        // if canceled → go to the cancel page.
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:4200/payments/success?orderId=" + order.getId())
                .setCancelUrl("http://localhost:4200/payments/cancel")
                .addAllLineItem(lineItems)
                .putMetadata("orderId", order.getId().toString())
                .build();


        // creates the stripe payment page for the order.
        // returns the payment page url.
        // if something goes wrong, it throws an error.
        try {
            Session session = Session.create(params);

            return new PaymentResponseDTO(session.getUrl());

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to create Stripe checkout session", e
            );
        }
    }

    public void markOrderAsPaid(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found !"));

        order.setStatus("PAID");
        orderRepository.save(order);

//        clear the cart after successfully payment
        User user = order.getUser();
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartNotFoundException("Cart not found !"));
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    public void handleWebhook(String payload, String sigHeader) {

        try {
            Event event = Webhook.constructEvent(
                    payload,
                    sigHeader,
                    webhookSecret
            );

            if ("checkout.session.completed".equals(event.getType())) {

                Session session = (Session) event
                        .getDataObjectDeserializer()
                        .getObject()
                        .orElseThrow();

                String orderId = session.getMetadata().get("orderId");

                markOrderAsPaid(Integer.valueOf(orderId));
            }

        } catch (Exception e) {
            throw new RuntimeException("Webhook error", e);
        }
    }
}