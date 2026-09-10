package com.waggy.service;

import com.waggy.dto.cart.CartItemRequestDTO;
import com.waggy.dto.cart.CartResponseDTO;
import com.waggy.entity.*;
import com.waggy.exception.*;
import com.waggy.mapper.CartMapper;
import com.waggy.repository.CartItemRepository;
import com.waggy.repository.CartRepository;
import com.waggy.repository.ProductRepository;
import com.waggy.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;


    private User getUser() {
        // get the email of the currently logged-in user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found !"));
    }


    public CartResponseDTO addCartInDb() {
        User user = getUser();

        return cartRepository.findByUser(user)
                .map(cartMapper::toDTO)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartMapper.toDTO(cartRepository.save(cart));
                });
    }


    public List<CartResponseDTO> findAllCarts() {
        User user = getUser();

        if (user.getRole().equals(Role.ADMIN)) {
            return cartRepository.findAll().stream().map(cartMapper::toDTO).toList();
        } else {
            return cartRepository.findByUserId(user.getId())
                    .map(cartMapper::toDTO)
                    .map(List::of)
                    .orElse(List.of());
        }
    }

    public CartResponseDTO findCartById(Integer id) {
        User user = getUser();

        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFoundException("Cart not found !"));

        if (!user.getRole().equals(Role.ADMIN)
                && !cart.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You cannot access this cart !");
        }

        return cartMapper.toDTO(cart);
    }


    public CartResponseDTO findMyCart() {
        User user = getUser();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        return cartMapper.toDTO(cart);

    }


    public CartResponseDTO addItemToCart(CartItemRequestDTO dto) {
        User user = getUser();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found!"));

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        int newQuantity = dto.quantity();

        if (cartItem != null) {
            newQuantity += cartItem.getQuantity();
        }

        if (newQuantity > product.getStock()) {
            throw new IllegalArgumentException("Not enough stock");
        }

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
        }

        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);

        return cartMapper.toDTO(cart);
    }


    public CartResponseDTO updateCartItem(Integer productId, CartItemRequestDTO dto) {
        User user = getUser();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found !"));

        Product product = cartItem.getProduct();

        if (dto.quantity() > product.getStock()) {
            throw new IllegalArgumentException("Not enough stock");
        }

        cartItem.setQuantity(dto.quantity());
        cartItemRepository.save(cartItem);

        return cartMapper.toDTO(cart);
    }


    public CartResponseDTO removeCartItem(Integer productId) {
        User user = getUser();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found !"));

        cartItemRepository.delete(cartItem);

        return cartMapper.toDTO(cart);
    }

    @Transactional
    public void deleteCart(Integer id) {
        User user = getUser();

        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new CartNotFoundException("Cart not found !"));

        if (!user.getRole().equals(Role.ADMIN)
                && !cart.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You cannot delete this cart !");
        }

        cart.getUser().setCart(null);
        cartItemRepository.deleteAllByCartId(cart.getId());
        cartRepository.delete(cart);
    }

    @Transactional
    public void clearCart() {
        User user = getUser();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        cartItemRepository.deleteAllByCartId(cart.getId());
    }

}
