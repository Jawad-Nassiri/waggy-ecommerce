package com.waggy.controller;


import com.waggy.dto.cart.CartItemRequestDTO;
import com.waggy.dto.cart.CartResponseDTO;
import com.waggy.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/carts")
@RestController
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CartController {
    private final CartService cartService;


    @Operation(summary = "Create cart", description = "Creates a new shopping cart.")
    @PostMapping
    public CartResponseDTO createCart() {
        return cartService.addCartInDb();
    }


    @Operation(summary = "Add item to cart", description = "Adds a product to the authenticated user's cart.")
    @PostMapping("/items")
    public CartResponseDTO addItemToCart(@Valid @RequestBody CartItemRequestDTO dto) {
        return cartService.addItemToCart(dto);
    }


    @Operation(summary = "Get all carts", description = "Returns all shopping carts.")
    @GetMapping
    public List<CartResponseDTO> getAllCarts() {
        return cartService.findAllCarts();
    }


    @Operation(summary = "Get cart by ID", description = "Returns the cart with the specified ID.")
    @GetMapping("/{id}")
    public CartResponseDTO getCartById(@PathVariable Integer id) {
        return cartService.findCartById(id);
    }


    @Operation(summary = "Get my cart", description = "Returns the authenticated user's shopping cart.")
    @GetMapping("/me")
    public CartResponseDTO getMyCart() {
        return cartService.findMyCart();
    }


    @Operation(summary = "Update cart item", description = "Updates the quantity of a product in the cart.")
    @PutMapping("/items/{productId}")
    public CartResponseDTO updateCartItem(
            @PathVariable Integer productId,
            @Valid @RequestBody CartItemRequestDTO dto) {
        return cartService.updateCartItem(productId, dto);
    }


    @Operation(summary = "Delete cart", description = "Deletes the cart with the specified ID.")
    @DeleteMapping("/{id}")
    public void deleteACart(@PathVariable Integer id) {
        cartService.deleteCart(id);
    }


    @Operation(summary = "Remove cart item", description = "Removes a product from the cart.")
    @DeleteMapping("/items/{productId}")
    public CartResponseDTO removeCartItem(@PathVariable Integer productId) {
        return cartService.removeCartItem(productId);
    }


    @Operation(summary = "Clear cart", description = "Removes all items from the authenticated user's cart.")
    @DeleteMapping("/me/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart() {
        cartService.clearCart();
    }

}
