package com.waggy.mapper;

import com.waggy.dto.product.ProductRequestDTO;
import com.waggy.dto.product.ProductResponseDTO;
import com.waggy.entity.Category;
import com.waggy.entity.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    private final CategoryMapper categoryMapper = new CategoryMapper();
    private final ProductMapper productMapper = new ProductMapper(categoryMapper);

    @Test
    void shouldMapRequestDtoToEntity() {

        ProductRequestDTO dto = new ProductRequestDTO(
                "Dog Food",
                "Food for dogs",
                BigDecimal.valueOf(25.99),
                10,
                "dog-food.jpg",
                1
        );

        Category category = new Category();
        category.setId(1);
        category.setName("Dog Food");
        category.setSlug("dog-food");

        Product product = productMapper.toEntity(dto, category);

        assertEquals("Dog Food", product.getName());
        assertEquals("Food for dogs", product.getDescription());
        assertEquals(BigDecimal.valueOf(25.99), product.getPrice());
        assertEquals(10, product.getStock());
        assertEquals("dog-food.jpg", product.getImage());
        assertEquals(category, product.getCategory());
    }

    @Test
    void shouldMapEntityToResponseDto() {

        Category category = new Category();
        category.setId(1);
        category.setName("Dog Food");
        category.setSlug("dog-food");

        Product product = new Product();
        product.setId(1);
        product.setName("Dog Food");
        product.setDescription("Food for dogs");
        product.setPrice(BigDecimal.valueOf(25.99));
        product.setStock(10);
        product.setImage("dog-food.jpg");
        product.setCategory(category);

        ProductResponseDTO dto = productMapper.toDTO(product);

        assertEquals(1, dto.id());
        assertEquals("Dog Food", dto.name());
        assertEquals("Food for dogs", dto.description());
        assertEquals(BigDecimal.valueOf(25.99), dto.price());
        assertEquals(10, dto.stock());
        assertEquals("dog-food.jpg", dto.image());

        assertEquals(1, dto.category().id());
        assertEquals("Dog Food", dto.category().name());
        assertEquals("dog-food", dto.category().slug());
    }
}