package com.waggy.mapper;

import com.waggy.dto.cart.CartResponseDTO;
import com.waggy.entity.Cart;
import com.waggy.entity.CartItem;
import com.waggy.entity.Product;
import com.waggy.entity.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartMapperTest {

    private final CartMapper cartMapper = new CartMapper(new CartItemMapper());

    @Test
    void shouldMapEntityToResponseDto() {
        User user = new User();
        user.setId(1);

        Product product = new Product();
        product.setId(10);
        product.setName("Dog Food");
        product.setPrice(new BigDecimal("29.99"));

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        Cart cart = new Cart();
        cart.setId(5);
        cart.setUser(user);
        cart.setCartItems(List.of(cartItem));

        CartResponseDTO dto = cartMapper.toDTO(cart);

        assertEquals(5, dto.id());
        assertEquals(1, dto.userId());
        assertEquals(1, dto.items().size());
        assertEquals(10, dto.items().get(0).productId());
        assertEquals(2, dto.items().get(0).quantity());
    }
}