package com.waggy.service;

import com.waggy.dto.category.CategoryRequestDTO;
import com.waggy.dto.category.CategoryResponseDTO;
import com.waggy.entity.Category;
import com.waggy.exception.CategoryNotFoundException;
import com.waggy.exception.DuplicateCategoryException;
import com.waggy.mapper.CategoryMapper;
import com.waggy.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {
    // add dependencies
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    // create a new category
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


    // get all categories
    public List<CategoryResponseDTO> findAllCategories() {
        return categoryRepository.findAll().stream().map(categoryMapper::toDTO).toList();
    }

    // get a user by ID
    public CategoryResponseDTO findCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found !"));

        return categoryMapper.toDTO(category);
    }

    // delete a category by ID
    public void deleteCategory(Integer id) {
       Category category = categoryRepository.findById(id)
               .orElseThrow(() -> new CategoryNotFoundException("Category not found !"));

       categoryRepository.delete(category);
    }

    // update category
    public CategoryResponseDTO updateCategory(Integer id, CategoryRequestDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found !"));

        if (categoryRepository.existsByName(dto.name())
                && !category.getName().equals(dto.name())) {
            throw new DuplicateCategoryException("Category name already exists");
        }

        if (categoryRepository.existsBySlug(dto.slug())
                && !category.getSlug().equals(dto.slug())) {
            throw new DuplicateCategoryException("Category slug already exists");
        }

        category.setName(dto.name());
        category.setSlug(dto.slug());

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toDTO(savedCategory);
    }

}
