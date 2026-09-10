package com.waggy.controller;


import com.waggy.dto.cart.CartItemRequestDTO;
import com.waggy.dto.cart.CartResponseDTO;
import com.waggy.service.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/carts")
@RestController
@AllArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping
    public CartResponseDTO createCart() {
        return cartService.addCartInDb();
    }

    @PostMapping("/items")
    public CartResponseDTO addItemToCart(@Valid @RequestBody CartItemRequestDTO dto) {
        return cartService.addItemToCart(dto);
    }

    @GetMapping
    public List<CartResponseDTO> getAllCarts() {
        return cartService.findAllCarts();
    }

    @GetMapping("/{id}")
    public CartResponseDTO getCartById(@PathVariable Integer id) {
        return cartService.findCartById(id);
    }


    @GetMapping("/me")
    public CartResponseDTO getMyCart() {
        return cartService.findMyCart();
    }


    @PutMapping("/items/{productId}")
    public CartResponseDTO updateCartItem(
            @PathVariable Integer productId,
            @Valid @RequestBody CartItemRequestDTO dto) {
        return cartService.updateCartItem(productId, dto);
    }


    @DeleteMapping("/{id}")
    public void deleteACart(@PathVariable Integer id) {
        cartService.deleteCart(id);
    }


    @DeleteMapping("/items/{productId}")
    public CartResponseDTO removeCartItem(@PathVariable Integer productId) {
        return cartService.removeCartItem(productId);
    }

    @DeleteMapping("/me/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart() {
        cartService.clearCart();
    }

}
