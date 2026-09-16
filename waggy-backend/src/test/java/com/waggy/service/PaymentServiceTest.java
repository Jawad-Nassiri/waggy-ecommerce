package com.waggy.service;

import com.stripe.net.Webhook;
import com.waggy.entity.Order;
import com.waggy.entity.Product;
import com.waggy.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import com.stripe.model.checkout.Session;
import com.waggy.dto.payment.PaymentRequestDTO;
import com.waggy.dto.payment.PaymentResponseDTO;
import com.waggy.entity.OrderItem;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Order createOrder() {
        Order order = new Order();
        order.setId(1);
        order.setStatus("PENDING");
        return order;
    }

    @Test
    void markOrderAsPaid_shouldMarkOrderAsPaid() {
        Order order = createOrder();

        when(orderRepository.findById(order.getId()))
                .thenReturn(Optional.of(order));

        paymentService.markOrderAsPaid(order.getId());

        assertEquals("PAID", order.getStatus());

        verify(orderRepository).findById(order.getId());
        verify(orderRepository).save(order);
    }

    @Test
    void createCheckoutSession_shouldCreateSession() {
        Order order = createOrder();

        Product product = new Product();
        product.setId(1);
        product.setName("PC");

        OrderItem item = new OrderItem();
        item.setQuantity(2);
        item.setPrice(new BigDecimal("29.99"));
        item.setProduct(product);

        order.setOrderItems(List.of(item));

        PaymentRequestDTO dto = new PaymentRequestDTO(order.getId());

        Session session = mock(Session.class);
        when(session.getUrl()).thenReturn("https://checkout.stripe.com/test");

        when(orderRepository.findById(order.getId()))
                .thenReturn(Optional.of(order));

        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {

            mockedSession.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenReturn(session);

            PaymentResponseDTO result =
                    paymentService.createCheckoutSession(dto);

            assertEquals("https://checkout.stripe.com/test", result.checkoutUrl());

            verify(orderRepository).findById(order.getId());
        }
    }

    @Test
    void handleWebhook_shouldMarkOrderAsPaid() {
        Order order = createOrder();

        Event event = mock(Event.class);
        EventDataObjectDeserializer deserializer =
                mock(EventDataObjectDeserializer.class);
        Session session = mock(Session.class);

        when(event.getType())
                .thenReturn("checkout.session.completed");

        when(event.getDataObjectDeserializer())
                .thenReturn(deserializer);

        when(deserializer.getObject())
                .thenReturn(Optional.of(session));

        when(session.getMetadata())
                .thenReturn(Map.of("orderId", "1"));

        when(orderRepository.findById(1))
                .thenReturn(Optional.of(order));

        try (MockedStatic<Webhook> mockedWebhook = mockStatic(Webhook.class)) {

            mockedWebhook.when(() ->
                    Webhook.constructEvent(
                            "payload",
                            "signature",
                            null
                    )
            ).thenReturn(event);

            paymentService.handleWebhook("payload", "signature");

            assertEquals("PAID", order.getStatus());

            verify(orderRepository).findById(1);
            verify(orderRepository).save(order);
        }
    }

}