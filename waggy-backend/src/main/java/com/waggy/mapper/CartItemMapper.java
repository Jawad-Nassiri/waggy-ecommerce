package com.waggy.mapper;


import com.waggy.dto.cart.CartItemRequestDTO;
import com.waggy.dto.cart.CartItemResponseDTO;
import com.waggy.entity.CartItem;
import com.waggy.entity.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

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
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity()))
        );
    }
}
