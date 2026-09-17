package com.waggy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waggy.dto.category.CategoryResponseDTO;
import com.waggy.dto.product.ProductRequestDTO;
import com.waggy.dto.product.ProductResponseDTO;
import com.waggy.service.JwtService;
import com.waggy.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private ProductService productService;

    private ProductRequestDTO createRequestDTO() {
        return new ProductRequestDTO(
                "Dog Food",
                "Premium food for dogs",
                new BigDecimal("29.99"),
                50,
                "dog-food.jpg",
                1
        );
    }

    private ProductResponseDTO createResponseDTO() {
        return new ProductResponseDTO(
                1,
                "Dog Food",
                "Premium food for dogs",
                new BigDecimal("29.99"),
                50,
                "dog-food.jpg",
                new CategoryResponseDTO(
                        1,
                        "Dog Food",
                        "dog_food"
                )
        );
    }

    private void assertProductResponse(ResultActions result) throws Exception {
        result
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Dog Food"))
                .andExpect(jsonPath("$.description").value("Premium food for dogs"))
                .andExpect(jsonPath("$.price").value(29.99))
                .andExpect(jsonPath("$.stock").value(50))
                .andExpect(jsonPath("$.image").value("dog-food.jpg"))
                .andExpect(jsonPath("$.category.id").value(1))
                .andExpect(jsonPath("$.category.name").value("Dog Food"))
                .andExpect(jsonPath("$.category.slug").value("dog_food"));
    }

    @Test
    void createProduct_shouldReturnCreatedProduct() throws Exception {
        ProductRequestDTO request = createRequestDTO();
        ProductResponseDTO response = createResponseDTO();

        when(productService.saveProductInDb(request)).thenReturn(response);

        assertProductResponse(
                mockMvc.perform(post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
        );

        verify(productService).saveProductInDb(request);
    }

    @Test
    void getAllProducts_shouldReturnProducts() throws Exception {
        ProductResponseDTO product = createResponseDTO();
        List<ProductResponseDTO> products = List.of(product);

        when(productService.findAllProducts()).thenReturn(products);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Dog Food"))
                .andExpect(jsonPath("$[0].description").value("Premium food for dogs"))
                .andExpect(jsonPath("$[0].price").value(29.99))
                .andExpect(jsonPath("$[0].stock").value(50))
                .andExpect(jsonPath("$[0].image").value("dog-food.jpg"))
                .andExpect(jsonPath("$[0].category.id").value(1))
                .andExpect(jsonPath("$[0].category.name").value("Dog Food"))
                .andExpect(jsonPath("$[0].category.slug").value("dog_food"));

        verify(productService).findAllProducts();
    }

    @Test
    void getProductById_shouldReturnProduct() throws Exception {
        Integer id = 1;
        ProductResponseDTO response = createResponseDTO();

        when(productService.findProductById(id)).thenReturn(response);

        assertProductResponse(
                mockMvc.perform(get("/products/{id}", id))
                        .andExpect(status().isOk())
        );

        verify(productService).findProductById(id);
    }

    @Test
    void updateProduct_shouldReturnUpdatedProduct() throws Exception {
        Integer id = 1;
        ProductRequestDTO request = createRequestDTO();
        ProductResponseDTO response = createResponseDTO();

        when(productService.updateProduct(id, request)).thenReturn(response);

        assertProductResponse(
                mockMvc.perform(put("/products/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
        );

        verify(productService).updateProduct(id, request);
    }

    @Test
    void deleteProduct_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(1);
    }
}