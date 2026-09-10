package com.waggy.service;

import com.waggy.dto.order.OrderItemRequestDTO;
import com.waggy.dto.order.OrderRequestDTO;
import com.waggy.dto.order.OrderResponseDTO;
import com.waggy.entity.Order;
import com.waggy.entity.OrderItem;
import com.waggy.entity.Product;
import com.waggy.entity.User;
import com.waggy.exception.OrderNotFoundException;
import com.waggy.exception.ProductNotFoundException;
import com.waggy.exception.UserNotFoundException;
import com.waggy.mapper.OrderMapper;
import com.waggy.repository.OrderRepository;
import com.waggy.repository.ProductRepository;
import com.waggy.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;


@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderResponseDTO createOrder(OrderRequestDTO dto) {
        // get the email of the currently logged-in user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found !"));

        Order order = orderMapper.toEntity();
        order.setUser(user);

        for (OrderItemRequestDTO item : dto.items()) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new ProductNotFoundException("Product not found !"));

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(item.quantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }


        Order savedOrder = orderRepository.save(order);

        return orderMapper.toDTO(savedOrder);
    }


    public List<OrderResponseDTO> findAllOrders() {
        return orderRepository.findAll()
                .stream().map(orderMapper::toDTO).toList();
    }


    public OrderResponseDTO findOrderById(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found !"));

        return orderMapper.toDTO(order);
    }


    public void deleteOrder(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found !"));

        orderRepository.delete(order);
    }

    public List<OrderResponseDTO> findMyOrders() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        return user.getOrders().stream().map(orderMapper::toDTO).toList();
    }

}
