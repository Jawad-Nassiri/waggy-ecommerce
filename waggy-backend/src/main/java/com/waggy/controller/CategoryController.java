package com.waggy.controller;

import com.waggy.dto.category.CategoryRequestDTO;
import com.waggy.dto.category.CategoryResponseDTO;
import com.waggy.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/categories")
@RestController
@AllArgsConstructor
public class CategoryController
{
    private final CategoryService categoryService;

    @PostMapping
    public CategoryResponseDTO createCategory(@Valid @RequestBody CategoryRequestDTO dto)  {
        return categoryService.saveCategoryInDb(dto);
    }

    @GetMapping
    public List<CategoryResponseDTO> findAllCategories() {
        return categoryService.findAllCategories();
    }

    @GetMapping("/{id}")
    public CategoryResponseDTO findCategoryById(@PathVariable Integer id) {
        return categoryService.findCategoryById(id);
    }

    @PutMapping("/{id}")
    public CategoryResponseDTO updateCategory(
            @PathVariable Integer id,
            @Valid @RequestBody CategoryRequestDTO dto) {
        return categoryService.updateCategory(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
    }
}
