package com.waggy.mapper;

import com.waggy.dto.category.CategoryRequestDTO;
import com.waggy.dto.category.CategoryResponseDTO;
import com.waggy.entity.Category;
import org.springframework.stereotype.Component;


@Component
public class CategoryMapper {
    public Category toEntity(CategoryRequestDTO dto) {
        Category category = new Category();
        category.setName(dto.name());
        category.setSlug(dto.slug());
        return category;
    }

    public CategoryResponseDTO toDTO(Category categoty) {
        return new CategoryResponseDTO(
                categoty.getId(),
                categoty.getName(),
                categoty.getSlug()
        );
    }
}
