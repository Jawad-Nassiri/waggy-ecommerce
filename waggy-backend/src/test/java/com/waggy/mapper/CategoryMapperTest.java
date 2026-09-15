package com.waggy.mapper;

import com.waggy.dto.category.CategoryRequestDTO;
import com.waggy.dto.category.CategoryResponseDTO;
import com.waggy.entity.Category;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CategoryMapperTest {

    private final CategoryMapper categoryMapper = new CategoryMapper();

    @Test
    void shouldMapRequestDtoToEntity() {
        CategoryRequestDTO dto = new CategoryRequestDTO(
                "dog Food",
                "dog_food"
        );

        Category category = categoryMapper.toEntity(dto);

        assertEquals("dog Food", category.getName());
        assertEquals("dog_food", category.getSlug());
    }


    @Test
    void shouldMapEntityToResponseDto() {
        Category category = new Category();
        category.setId(1);
        category.setName("dog Food");
        category.setSlug("dog_food");

        CategoryResponseDTO dto = categoryMapper.toDTO(category);

        assertEquals(1, dto.id());
        assertEquals("dog Food", dto.name());
        assertEquals("dog_food", dto.slug());

    }

}