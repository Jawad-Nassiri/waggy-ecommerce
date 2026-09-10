package com.waggy.controller;

import com.waggy.dto.order.OrderRequestDTO;
import com.waggy.dto.order.OrderResponseDTO;
import com.waggy.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/orders")
@RestController
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public OrderResponseDTO createOrder(@Valid @RequestBody OrderRequestDTO dto) {
        return orderService.createOrder(dto);
    }

    @GetMapping
    public List<OrderResponseDTO> getAllOrders() {
        return orderService.findAllOrders();
    }

    @GetMapping("/me")
    public List<OrderResponseDTO> getMyOrders() {
        return orderService.findMyOrders();
    }


    @GetMapping("/{id}")
    public OrderResponseDTO getOrderById(@PathVariable Integer id) {
        return orderService.findOrderById(id);
    }


    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Integer id) {
        orderService.deleteOrder(id);
    }
}
