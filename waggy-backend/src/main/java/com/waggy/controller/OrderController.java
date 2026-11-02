package com.waggy.controller;

import com.waggy.dto.order.OrderRequestDTO;
import com.waggy.dto.order.OrderResponseDTO;
import com.waggy.repository.OrderRepository;
import com.waggy.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/orders")
@RestController
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderRepository orderRepository;


    @Operation(summary = "Create order", description = "Creates a new order for the authenticated user.")
    @PostMapping
    public OrderResponseDTO createOrder(@Valid @RequestBody OrderRequestDTO dto) {
        return orderService.createOrder(dto);
    }


    @Operation(summary = "Get all orders", description = "Returns all orders in the system.")
    @GetMapping
    public List<OrderResponseDTO> getAllOrders() {
        return orderService.findAllOrders();
    }


    @Operation(summary = "Get my orders", description = "Returns all orders belonging to the authenticated user.")
    @GetMapping("/me")
    public List<OrderResponseDTO> getMyOrders() {
        return orderService.findMyOrders();
    }

    @Operation(summary = "Get order by ID", description = "Returns the order with the specified ID.")
    @GetMapping("/{id}")
    public OrderResponseDTO getOrderById(@PathVariable Integer id) {
        return orderService.findOrderById(id);
    }


    @Operation(summary = "Delete order", description = "Deletes the order with the specified ID.")
    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Integer id) {
        orderService.deleteOrder(id);
    }


    @Operation(summary = "Get orders count", description = "Returns the total number of orders.")
    @GetMapping("/count")
    public long getOrdersCount() {
    return orderRepository.count();
}

}
