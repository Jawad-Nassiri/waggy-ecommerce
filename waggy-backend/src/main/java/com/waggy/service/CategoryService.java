package com.waggy.service;

import com.waggy.dto.category.CategoryRequestDTO;
import com.waggy.dto.category.CategoryResponseDTO;
import com.waggy.entity.Category;
import com.waggy.exception.DuplicateCategoryException;
import com.waggy.mapper.CategoryMapper;
import com.waggy.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryResponseDTO saveCategoryInDb(CategoryRequestDTO dto) {

        if(categoryRepository.existsByName(dto.name())) {
            throw new DuplicateCategoryException("Category name already exists");
        }

        if(categoryRepository.existsBySlug(dto.slug())) {
            throw new DuplicateCategoryException("Category slug already exis");
        }

        Category category = categoryMapper.toEntity(dto);
        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toDTO(savedCategory);
    }

    public List<CategoryResponseDTO> findAllCategories() {
        return categoryRepository.findAll().stream().map(categoryMapper::toDTO).toList();
    }
}
