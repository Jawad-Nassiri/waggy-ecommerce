package com.waggy.service;

import com.waggy.dto.order.OrderItemRequestDTO;
import com.waggy.dto.order.OrderResponseDTO;
import com.waggy.dto.order.OrderRequestDTO;
import com.waggy.entity.Order;
import com.waggy.entity.Product;
import com.waggy.entity.User;
import com.waggy.mapper.OrderMapper;
import com.waggy.repository.OrderRepository;
import com.waggy.repository.ProductRepository;
import com.waggy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;


    @Test
    void createOrder_shouldCreateOrder() {

        OrderItemRequestDTO orderItemRequestDTO =
                new OrderItemRequestDTO(1, 2);

        OrderRequestDTO dto = new OrderRequestDTO(
                List.of(orderItemRequestDTO)
        );

        User user = createUser();
        Order order = createOrder();
        Product product = createProduct();
        Order savedOrder = createOrder();
        OrderResponseDTO responseDTO = createOrderResponseDTO();

        setAuthentication();

        when(userRepository.findByEmail("jawad@test.com"))
                .thenReturn(Optional.of(user));
        when(orderMapper.toEntity())
                .thenReturn(order);
        when(productRepository.findById(product.getId()))
                .thenReturn(Optional.of(product));
        when(orderRepository.save(order))
                .thenReturn(savedOrder);
        when(orderMapper.toDTO(savedOrder))
                .thenReturn(responseDTO);

        OrderResponseDTO result = orderService.createOrder(dto);

        assertEquals(responseDTO, result);

        verify(userRepository).findByEmail("jawad@test.com");
        verify(orderMapper).toEntity();
        verify(authentication).getName();
        verify(productRepository).findById(product.getId());
        verify(orderRepository).save(order);
        verify(orderMapper).toDTO(savedOrder);
    }

    @Test
    void findAllOrders_shouldFindAllOrders() {

        Order order = createOrder();
        OrderResponseDTO responseDTO = createOrderResponseDTO();

        when(orderRepository.findAll())
                .thenReturn(List.of(order));
        when(orderMapper.toDTO(order))
                .thenReturn(responseDTO);

        List<OrderResponseDTO> result = orderService.findAllOrders();

        assertEquals(List.of(responseDTO), result);

        verify(orderRepository).findAll();
        verify(orderMapper).toDTO(order);
    }

    @Test
    void findOrderById_shouldFindOrderById() {

        Order order = createOrder();
        OrderResponseDTO responseDTO = createOrderResponseDTO();

        when(orderRepository.findById(order.getId()))
                .thenReturn(Optional.of(order));
        when(orderMapper.toDTO(order))
                .thenReturn(responseDTO);

        OrderResponseDTO result =
                orderService.findOrderById(order.getId());

        assertEquals(responseDTO, result);

        verify(orderRepository).findById(order.getId());
        verify(orderMapper).toDTO(order);
    }

    @Test
    void deleteOrder_shouldDeleteOrder() {

        Order order = createOrder();

        when(orderRepository.findById(order.getId()))
                .thenReturn(Optional.of(order));

        orderService.deleteOrder(order.getId());

        verify(orderRepository).findById(order.getId());
        verify(orderRepository).delete(order);
    }

    @Test
    void findMyOrders_shouldFindMyOrders() {

        User user = createUser();
        Order order = createOrder();
        OrderResponseDTO responseDTO = createOrderResponseDTO();

        user.setOrders(List.of(order));

        setAuthentication();

        when(userRepository.findByEmail("jawad@test.com"))
                .thenReturn(Optional.of(user));
        when(orderMapper.toDTO(order))
                .thenReturn(responseDTO);

        List<OrderResponseDTO> result =
                orderService.findMyOrders();

        assertEquals(List.of(responseDTO), result);

        verify(authentication).getName();
        verify(userRepository).findByEmail("jawad@test.com");
        verify(orderMapper).toDTO(order);
    }

    private void setAuthentication() {
        when(authentication.getName())
                .thenReturn("jawad@test.com");

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    private User createUser() {
        User user = new User();
        user.setId(1);
        user.setName("jawad");
        user.setEmail("jawad@test.com");

        return user;
    }

    private Order createOrder() {
        Order order = new Order();
        order.setId(1);

        return order;
    }

    private Product createProduct() {
        Product product = new Product();
        product.setId(1);
        product.setName("PC");
        product.setPrice(new BigDecimal("29.99"));
        product.setStock(100);

        return product;
    }

    private OrderResponseDTO createOrderResponseDTO() {
        return new OrderResponseDTO(
                1,
                LocalDateTime.parse("2026-09-16T00:00:00"),
                List.of(),
                new BigDecimal("59.98"),
                "PENDING"
        );
    }
}