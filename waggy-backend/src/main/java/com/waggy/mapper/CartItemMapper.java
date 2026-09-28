package com.waggy.mapper;


import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.waggy.dto.cart.CartItemRequestDTO;
import com.waggy.dto.cart.CartItemResponseDTO;
import com.waggy.entity.CartItem;
import com.waggy.entity.Product;

@Component
public class CartItemMapper {
    public CartItem toEntity(CartItemRequestDTO dto, Product product) {
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(dto.quantity());
        return cartItem;
    }

    public CartItemResponseDTO toDTO(CartItem cartItem) {
        return new CartItemResponseDTO(
                cartItem.getProduct().getId(),
                cartItem.getProduct().getName(),
                cartItem.getQuantity(),
                cartItem.getProduct().getPrice(),
                cartItem.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity())),
                cartItem.getProduct().getImage()
        );
    }
}
