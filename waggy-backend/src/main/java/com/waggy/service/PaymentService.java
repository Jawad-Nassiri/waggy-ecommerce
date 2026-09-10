package com.waggy.service;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.waggy.dto.payment.PaymentRequestDTO;
import com.waggy.dto.payment.PaymentResponseDTO;
import com.waggy.entity.Order;
import com.waggy.exception.OrderNotFoundException;
import com.waggy.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;

    public PaymentResponseDTO createCheckoutSession(PaymentRequestDTO dto) {

        Order order = orderRepository.findById(dto.orderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found !"));

//        This part takes all the items in the order and converts each one into a Stripe line item.
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

                // This part sets the payment details.
                // After payment → go to the success page.
                // If cancelled → go to the cancel page.
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:4200/payment/success")
                .setCancelUrl("http://localhost:4200/payment/cancel")
                .addAllLineItem(lineItems)
                .build();


        // creates the Stripe payment page for the order.
        // returns the payment page URL.
        // If something goes wrong, it throws an error.
        try {
            Session session = Session.create(params);

            return new PaymentResponseDTO(session.getUrl());

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to create Stripe checkout session", e
            );
        }
    }
}