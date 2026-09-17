package com.waggy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waggy.dto.order.OrderItemRequestDTO;
import com.waggy.dto.order.OrderItemResponseDTO;
import com.waggy.dto.order.OrderRequestDTO;
import com.waggy.dto.order.OrderResponseDTO;
import com.waggy.service.JwtService;
import com.waggy.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtService jwtService;

    private OrderRequestDTO createRequestDTO() {
        return new OrderRequestDTO(
                List.of(
                        new OrderItemRequestDTO(1, 2)
                )
        );
    }

    private OrderItemResponseDTO createItemResponseDTO() {
        return new OrderItemResponseDTO(
                1,
                "Dog Food",
                2,
                new BigDecimal("29.99"),
                new BigDecimal("59.98")
        );
    }

    private OrderResponseDTO createResponseDTO() {
        return new OrderResponseDTO(
                1,
                LocalDateTime.of(2026, 9, 17, 10, 30),
                List.of(createItemResponseDTO()),
                new BigDecimal("59.98"),
                "PENDING"
        );
    }

    @Test
    void createOrder_shouldReturnCreatedOrder() throws Exception {
        OrderRequestDTO request = createRequestDTO();
        OrderResponseDTO response = createResponseDTO();

        when(orderService.createOrder(request)).thenReturn(response);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderDate").value("2026-09-17T10:30:00"))
                .andExpect(jsonPath("$.items[0].productId").value(1))
                .andExpect(jsonPath("$.items[0].productName").value("Dog Food"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].price").value(29.99))
                .andExpect(jsonPath("$.items[0].subtotal").value(59.98))
                .andExpect(jsonPath("$.totalPrice").value(59.98))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderService).createOrder(request);
    }

    @Test
    void getAllOrders_shouldReturnOrders() throws Exception {
        OrderResponseDTO response = createResponseDTO();

        when(orderService.findAllOrders()).thenReturn(List.of(response));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].orderDate").value("2026-09-17T10:30:00"))
                .andExpect(jsonPath("$[0].items[0].productName").value("Dog Food"))
                .andExpect(jsonPath("$[0].items[0].quantity").value(2))
                .andExpect(jsonPath("$[0].totalPrice").value(59.98))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(orderService).findAllOrders();
    }

    @Test
    void getMyOrders_shouldReturnOrders() throws Exception {
        OrderResponseDTO response = createResponseDTO();

        when(orderService.findMyOrders()).thenReturn(List.of(response));

        mockMvc.perform(get("/orders/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].orderDate").value("2026-09-17T10:30:00"))
                .andExpect(jsonPath("$[0].items[0].productId").value(1))
                .andExpect(jsonPath("$[0].items[0].productName").value("Dog Food"))
                .andExpect(jsonPath("$[0].items[0].quantity").value(2))
                .andExpect(jsonPath("$[0].items[0].price").value(29.99))
                .andExpect(jsonPath("$[0].items[0].subtotal").value(59.98))
                .andExpect(jsonPath("$[0].totalPrice").value(59.98))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(orderService).findMyOrders();
    }

    @Test
    void getOrderById_shouldReturnOrder() throws Exception {
        Integer id = 1;
        OrderResponseDTO response = createResponseDTO();

        when(orderService.findOrderById(id)).thenReturn(response);

        mockMvc.perform(get("/orders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderDate").value("2026-09-17T10:30:00"))
                .andExpect(jsonPath("$.items[0].productId").value(1))
                .andExpect(jsonPath("$.items[0].productName").value("Dog Food"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].price").value(29.99))
                .andExpect(jsonPath("$.items[0].subtotal").value(59.98))
                .andExpect(jsonPath("$.totalPrice").value(59.98))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderService).findOrderById(id);
    }

    @Test
    void deleteOrder_shouldReturnOk() throws Exception {
        Integer id = 1;

        mockMvc.perform(delete("/orders/{id}", id))
                .andExpect(status().isOk());

        verify(orderService).deleteOrder(id);
    }
}