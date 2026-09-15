package com.waggy.mapper;

import com.waggy.dto.cart.CartItemRequestDTO;
import com.waggy.dto.cart.CartItemResponseDTO;
import com.waggy.entity.CartItem;
import com.waggy.entity.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;


class CartItemMapperTest {
    private final CartItemMapper cartItemMapper = new CartItemMapper();

    @Test
    void shouldMapRequestDtoToEntity() {
        Product product = new Product();
        product.setId(1);

        CartItemRequestDTO dto = new CartItemRequestDTO(1, 2);

        CartItem cartItem = cartItemMapper.toEntity(dto, product);

        assertEquals(1, cartItem.getProduct().getId());
        assertEquals(2, cartItem.getQuantity());

    }


    @Test
    void shouldMapEntityToResponseDto() {
        Product product = new Product();
        product.setId(1);
        product.setPrice(new BigDecimal("29.99"));

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        CartItemResponseDTO dto = cartItemMapper.toDTO(cartItem);

        assertEquals(product.getId(), dto.productId());
        assertEquals(2, dto.quantity());

    }
}