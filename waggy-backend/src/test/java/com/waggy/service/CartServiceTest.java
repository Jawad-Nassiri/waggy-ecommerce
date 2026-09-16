package com.waggy.service;

import com.waggy.dto.cart.CartItemRequestDTO;
import com.waggy.dto.cart.CartItemResponseDTO;
import com.waggy.dto.cart.CartResponseDTO;
import com.waggy.entity.*;
import com.waggy.mapper.CartMapper;
import com.waggy.repository.CartItemRepository;
import com.waggy.repository.CartRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private Authentication authentication;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartService cartService;

    private CartItemRequestDTO createCartItemRequestDTO() {
        return new CartItemRequestDTO(
                1,
                2
        );
    }

    private CartItemResponseDTO createCartItemResponse() {
        return new CartItemResponseDTO(
                1,
                "mac book",
                1,
                new BigDecimal(1299.99),
                new BigDecimal(1299.99)
        );
    }

    private CartResponseDTO createCartResponseDTO() {
        return new CartResponseDTO(
                1,
                1,
                List.of()
        );
    }

    private Cart createCart() {
        Cart cart = new Cart();
        cart.setId(1);
        cart.setCreatedAt(LocalDateTime.parse("2020-12-12T00:00:00"));

        User user = new User();
        user.setId(1);
        user.setEmail("jawad@test.com");

        cart.setUser(user);

        return cart;
    }

    private User createUser() {
        User user = new User();
        user.setId(1);
        user.setName("jawad");
        user.setEmail("jawad@test.com");
        user.setRole(Role.USER);

        return user;
    }

    private void setAuthentication() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("jawad@test.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void addCartInDb_shouldSaveCart() {
        User user = new User();
        Cart cart = new Cart();

        CartResponseDTO responseDTO = createCartResponseDTO();

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(authentication.getName()).thenReturn("jawad@test.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findByEmail("jawad@test.com")).thenReturn(Optional.of(user));
        when(cartMapper.toDTO(cart)).thenReturn(responseDTO);

        CartResponseDTO result = cartService.addCartInDb();

        assertEquals(responseDTO, result);
        verify(authentication).getName();
        verify(cartRepository).findByUser(user);
        verify(userRepository).findByEmail("jawad@test.com");
        verify(cartMapper).toDTO(cart);
    }

    @Test
    void findAllCarts_shouldFindCurrentUserCart() {
        User user = createUser();
        user.setRole(Role.USER);

        Cart cart = createCart();
        CartResponseDTO responseDTO = createCartResponseDTO();

        setAuthentication();

        when(userRepository.findByEmail("jawad@test.com"))
                .thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(cart));
        when(cartMapper.toDTO(cart))
                .thenReturn(responseDTO);

        List<CartResponseDTO> result = cartService.findAllCarts();

        assertEquals(List.of(responseDTO), result);

        verify(userRepository).findByEmail("jawad@test.com");
        verify(cartRepository).findByUserId(user.getId());
        verify(cartMapper).toDTO(cart);
    }

    @Test
    void findCartById_shouldFindById() {
        User user = createUser();
        Cart cart = createCart();
        CartResponseDTO cartResponseDTO = createCartResponseDTO();

        setAuthentication();
        when(userRepository.findByEmail("jawad@test.com")).thenReturn(Optional.of(user));
        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(cartMapper.toDTO(cart)).thenReturn(cartResponseDTO);

        CartResponseDTO result = cartService.findCartById(cart.getId());

        assertEquals(cartResponseDTO, result);
        verify(cartRepository).findById(cart.getId());
        verify(cartMapper).toDTO(cart);
        verify(userRepository).findByEmail("jawad@test.com");
    }

    @Test
    void findMyCart_shouldFindMyCart() {
        User user = createUser();
        Cart cart = createCart();
        CartResponseDTO cartResponseDTO = createCartResponseDTO();

        setAuthentication();
        when(userRepository.findByEmail("jawad@test.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(cartMapper.toDTO(cart)).thenReturn(cartResponseDTO);

        CartResponseDTO result = cartService.findMyCart();

        assertEquals(cartResponseDTO, result);
        verify(userRepository).findByEmail("jawad@test.com");
        verify(cartRepository).findByUserId(user.getId());
        verify(cartMapper).toDTO(cart);
    }

    @Test
    void addItemToCart_shouldAddItem() {
        User user = createUser();
        Cart cart = createCart();
        Product product = new Product();
        product.setId(1);
        product.setName("pc");
        product.setDescription("description");
        product.setPrice(new BigDecimal("79.99"));
        product.setStock(100);
        product.setImage("image.png");
        product.setCreatedAt(LocalDateTime.parse("2020-12-12T00:00:00"));
        CartResponseDTO responseDTO = createCartResponseDTO();

        CartItemRequestDTO dto = new CartItemRequestDTO(
                product.getId(),
                2
        );

        setAuthentication();

        when(userRepository.findByEmail("jawad@test.com"))
                .thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId()))
                .thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(
                cart.getId(), product.getId()))
                .thenReturn(Optional.empty());
        when(cartMapper.toDTO(cart))
                .thenReturn(responseDTO);

        CartResponseDTO result = cartService.addItemToCart(dto);

        assertEquals(responseDTO, result);

        verify(userRepository).findByEmail("jawad@test.com");
        verify(cartRepository).findByUserId(user.getId());
        verify(productRepository).findById(product.getId());
        verify(cartItemRepository)
                .findByCartIdAndProductId(cart.getId(), product.getId());
        verify(cartItemRepository).save(any(CartItem.class));
        verify(cartMapper).toDTO(cart);
    }

    @Test
    void updateCartItem_shouldUpdateQuantity() {
        User user = createUser();
        Cart cart = createCart();
        Product product = new Product();
        product.setId(1);
        product.setName("pc");
        product.setDescription("description");
        product.setPrice(new BigDecimal("79.99"));
        product.setStock(100);
        product.setImage("image.png");
        product.setCreatedAt(LocalDateTime.parse("2020-12-12T00:00:00"));
        CartItem cartItem = new CartItem();
        CartResponseDTO responseDTO = createCartResponseDTO();

        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        CartItemRequestDTO dto = new CartItemRequestDTO(
                product.getId(),
                5
        );

        setAuthentication();

        when(userRepository.findByEmail("jawad@test.com"))
                .thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(
                cart.getId(), product.getId()))
                .thenReturn(Optional.of(cartItem));
        when(cartMapper.toDTO(cart))
                .thenReturn(responseDTO);

        CartResponseDTO result = cartService.updateCartItem(product.getId(), dto);

        assertEquals(responseDTO, result);

        verify(userRepository).findByEmail("jawad@test.com");
        verify(cartRepository).findByUserId(user.getId());
        verify(cartItemRepository)
                .findByCartIdAndProductId(cart.getId(), product.getId());
        verify(cartItemRepository).save(cartItem);
        verify(cartMapper).toDTO(cart);
    }

    @Test
    void removeCartItem_shouldRemoveItem() {
        User user = createUser();
        Cart cart = createCart();
        CartItem cartItem = new CartItem();
        CartResponseDTO responseDTO = createCartResponseDTO();

        cartItem.setCart(cart);

        setAuthentication();

        when(userRepository.findByEmail("jawad@test.com"))
                .thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(
                cart.getId(), 1))
                .thenReturn(Optional.of(cartItem));
        when(cartMapper.toDTO(cart))
                .thenReturn(responseDTO);

        CartResponseDTO result = cartService.removeCartItem(1);

        assertEquals(responseDTO, result);

        verify(userRepository).findByEmail("jawad@test.com");
        verify(cartRepository).findByUserId(user.getId());
        verify(cartItemRepository)
                .findByCartIdAndProductId(cart.getId(), 1);
        verify(cartItemRepository).delete(cartItem);
        verify(cartMapper).toDTO(cart);
    }

    @Test
    void deleteCart_shouldDeleteOwnCart() {
        User user = createUser();
        user.setRole(Role.USER);

        Cart cart = createCart();
        cart.setUser(user);

        setAuthentication();

        when(userRepository.findByEmail("jawad@test.com"))
                .thenReturn(Optional.of(user));
        when(cartRepository.findById(cart.getId()))
                .thenReturn(Optional.of(cart));

        cartService.deleteCart(cart.getId());

        verify(userRepository).findByEmail("jawad@test.com");
        verify(cartRepository).findById(cart.getId());
        verify(cartItemRepository).deleteAllByCartId(cart.getId());
        verify(cartRepository).delete(cart);
    }

    @Test
    void clearCart_shouldClearCart() {
        User user = createUser();
        Cart cart = createCart();

        setAuthentication();

        when(userRepository.findByEmail("jawad@test.com"))
                .thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(cart));

        cartService.clearCart();

        verify(userRepository).findByEmail("jawad@test.com");
        verify(cartRepository).findByUserId(user.getId());
        verify(cartItemRepository).deleteAllByCartId(cart.getId());
    }
}