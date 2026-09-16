package com.waggy.service;

import com.waggy.dto.category.CategoryRequestDTO;
import com.waggy.dto.category.CategoryResponseDTO;
import com.waggy.entity.Category;
import com.waggy.mapper.CategoryMapper;
import com.waggy.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category createCategory() {
        Category category = new Category();
        category.setId(1);
        category.setName("Dog Food");
        category.setSlug("dog_food");
        return category;
    }

    private CategoryResponseDTO createCategoryResponse() {
        return new CategoryResponseDTO(
                1,
                "Dog Food",
                "dog_food"
        );
    }

    private CategoryRequestDTO createCategoryRequestDto() {
        return new CategoryRequestDTO("Dog Food", "dog_food");
    }

    @Test
    void saveCategoryInDb_shouldSaveCategory() {

        CategoryRequestDTO dto = createCategoryRequestDto();
        Category category = createCategory();
        Category savedCategory = createCategory();
        CategoryResponseDTO responseDTO = createCategoryResponse();


        when(categoryRepository.existsByName(dto.name())).thenReturn(false);
        when(categoryRepository.existsBySlug(dto.slug())).thenReturn(false);
        when(categoryMapper.toEntity(dto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(savedCategory);
        when(categoryMapper.toDTO(savedCategory)).thenReturn(responseDTO);

        CategoryResponseDTO result = categoryService.saveCategoryInDb(dto);

        assertEquals(responseDTO, result);
        verify(categoryRepository).existsByName(dto.name());
        verify(categoryRepository).existsBySlug(dto.slug());
        verify(categoryMapper).toEntity(dto);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toDTO(savedCategory);

    }

    @Test
    void findAllCategories_shouldFindAllCategories() {
        Category category = createCategory();
        CategoryResponseDTO categoryResponseDTO = createCategoryResponse();

        List<Category> categories = List.of(category);

        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toDTO(category)).thenReturn(categoryResponseDTO);

        List<CategoryResponseDTO> result = categoryService.findAllCategories();

        assertEquals(List.of(categoryResponseDTO), result);

        verify(categoryRepository).findAll();
        verify(categoryMapper).toDTO(category);
    }

    @Test
    void findCategoryById_shouldFindById() {
        Category category = createCategory();
        CategoryResponseDTO responseDTO = createCategoryResponse();

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryMapper.toDTO(category)).thenReturn(responseDTO);

        CategoryResponseDTO result = categoryService.findCategoryById(category.getId());

        assertEquals(responseDTO, result);
        verify(categoryRepository).findById(category.getId());
        verify(categoryMapper).toDTO(category);
    }

    @Test
    void deleteCategory_shouldDeleteCategory() {
        Category category = createCategory();

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        categoryService.deleteCategory(category.getId());

        verify(categoryRepository).findById(category.getId());
        verify(categoryRepository).delete(category);
    }

    @Test
    void updateCategory_shouldUpdateCategory() {
        Category category = createCategory();
        CategoryRequestDTO categoryRequestDTO = createCategoryRequestDto();
        CategoryResponseDTO categoryResponseDTO = createCategoryResponse();

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName(category.getName())).thenReturn(false);
        when(categoryRepository.existsBySlug(category.getSlug())).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDTO(category)).thenReturn(categoryResponseDTO);

        CategoryResponseDTO result = categoryService.updateCategory(category.getId(), categoryRequestDTO);

        assertEquals(result, categoryResponseDTO);
        verify(categoryRepository).findById(category.getId());
        verify(categoryRepository).existsByName(category.getName());
        verify(categoryRepository).existsBySlug(category.getSlug());
        verify(categoryRepository).save(category);
        verify(categoryMapper).toDTO(category);
    }

}