package com.waggy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waggy.dto.cart.CartItemRequestDTO;
import com.waggy.dto.cart.CartItemResponseDTO;
import com.waggy.dto.cart.CartResponseDTO;
import com.waggy.service.CartService;
import com.waggy.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CartService cartService;

    private CartItemRequestDTO createRequestDTO() {
        return new CartItemRequestDTO(
                1,
                2
        );
    }

    private CartItemResponseDTO createItemResponseDTO() {
        return new CartItemResponseDTO(
                1,
                "Dog Food",
                2,
                new BigDecimal("29.99"),
                new BigDecimal("59.98")
        );
    }

    private CartResponseDTO createResponseDTO() {
        return new CartResponseDTO(
                1,
                10,
                List.of(createItemResponseDTO())
        );
    }


    @Test
    void createCart_shouldReturnCreatedCart() throws Exception {
        CartResponseDTO response = createResponseDTO();

        when(cartService.addCartInDb()).thenReturn(response);

        mockMvc.perform(post("/carts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].productId").value(1))
                .andExpect(jsonPath("$.items[0].productName").value("Dog Food"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].price").value(29.99))
                .andExpect(jsonPath("$.items[0].subtotal").value(59.98));

        verify(cartService).addCartInDb();
    }

    @Test
    void addItemToCart_shouldReturnCart() throws Exception {
        CartItemRequestDTO request = createRequestDTO();
        CartResponseDTO response = createResponseDTO();

        when(cartService.addItemToCart(request)).thenReturn(response);

        mockMvc.perform(post("/carts/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(jsonPath("$.items[0].productId").value(1))
                .andExpect(jsonPath("$.items[0].productName").value("Dog Food"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].price").value(29.99))
                .andExpect(jsonPath("$.items[0].subtotal").value(59.98));

        verify(cartService).addItemToCart(request);
    }

    @Test
    void getAllCarts_shouldReturnCarts() throws Exception {
        CartResponseDTO response = createResponseDTO();

        when(cartService.findAllCarts()).thenReturn(List.of(response));

        mockMvc.perform(get("/carts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userId").value(10))
                .andExpect(jsonPath("$[0].items[0].productId").value(1))
                .andExpect(jsonPath("$[0].items[0].productName").value("Dog Food"))
                .andExpect(jsonPath("$[0].items[0].quantity").value(2))
                .andExpect(jsonPath("$[0].items[0].price").value(29.99))
                .andExpect(jsonPath("$[0].items[0].subtotal").value(59.98));

        verify(cartService).findAllCarts();
    }

    @Test
    void getCartById_shouldReturnCart() throws Exception {
        Integer id = 1;
        CartResponseDTO response = createResponseDTO();

        when(cartService.findCartById(id)).thenReturn(response);

        mockMvc.perform(get("/carts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(jsonPath("$.items[0].productId").value(1))
                .andExpect(jsonPath("$.items[0].productName").value("Dog Food"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].price").value(29.99))
                .andExpect(jsonPath("$.items[0].subtotal").value(59.98));

        verify(cartService).findCartById(id);
    }

    @Test
    void getMyCart_shouldReturnCart() throws Exception {
        CartResponseDTO response = createResponseDTO();

        when(cartService.findMyCart()).thenReturn(response);

        mockMvc.perform(get("/carts/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(jsonPath("$.items[0].productId").value(1))
                .andExpect(jsonPath("$.items[0].productName").value("Dog Food"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].price").value(29.99))
                .andExpect(jsonPath("$.items[0].subtotal").value(59.98));

        verify(cartService).findMyCart();
    }

    @Test
    void updateCartItem_shouldReturnUpdatedCart() throws Exception {
        Integer productId = 1;
        CartItemRequestDTO request = createRequestDTO();
        CartResponseDTO response = createResponseDTO();

        when(cartService.updateCartItem(productId, request)).thenReturn(response);

        mockMvc.perform(put("/carts/items/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].subtotal").value(59.98));

        verify(cartService).updateCartItem(productId, request);
    }

    @Test
    void deleteCart_shouldReturnOk() throws Exception {
        Integer id = 1;

        mockMvc.perform(delete("/carts/{id}", id))
                .andExpect(status().isOk());

        verify(cartService).deleteCart(id);
    }

    @Test
    void removeCartItem_shouldReturnCart() throws Exception {
        Integer productId = 1;
        CartResponseDTO response = createResponseDTO();

        when(cartService.removeCartItem(productId)).thenReturn(response);

        mockMvc.perform(delete("/carts/items/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(jsonPath("$.items[0].productId").value(1))
                .andExpect(jsonPath("$.items[0].productName").value("Dog Food"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].price").value(29.99))
                .andExpect(jsonPath("$.items[0].subtotal").value(59.98));

        verify(cartService).removeCartItem(productId);
    }

    @Test
    void clearCart_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/carts/me/items"))
                .andExpect(status().isNoContent());

        verify(cartService).clearCart();
    }
}