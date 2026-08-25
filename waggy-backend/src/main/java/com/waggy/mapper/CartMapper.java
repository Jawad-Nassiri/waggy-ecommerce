package com.waggy.mapper;

import com.waggy.dto.cart.CartResponseDTO;
import com.waggy.entity.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartMapper {

    private final CartItemMapper cartItemMapper;

    public CartResponseDTO toDTO(Cart cart) {
        return new CartResponseDTO(
                cart.getId(),
                cart.getUser().getId(),
                cart.getCartItems()
                        .stream()
                        .map(cartItemMapper::toDTO)
                        .toList()
        );
    }
}
