package com.waggy.controller;

import com.waggy.dto.category.CategoryRequestDTO;
import com.waggy.dto.category.CategoryResponseDTO;
import com.waggy.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/categories")
@RestController
@AllArgsConstructor
public class CategoryController
{
    private final CategoryService categoryService;


    @Operation(summary = "Create category", description = "Creates a new product category.")
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public CategoryResponseDTO createCategory(@Valid @RequestBody CategoryRequestDTO dto)  {
        return categoryService.saveCategoryInDb(dto);
    }


    @Operation(summary = "Get all categories", description = "Returns all product categories.")
    @GetMapping
    public List<CategoryResponseDTO> findAllCategories() {
        return categoryService.findAllCategories();
    }


    @Operation(summary = "Get category by ID", description = "Returns the category with the specified ID.")
    @GetMapping("/{id}")
    public CategoryResponseDTO findCategoryById(@PathVariable Integer id) {
        return categoryService.findCategoryById(id);
    }


    @Operation(summary = "Update category", description = "Updates an existing category.")
    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public CategoryResponseDTO updateCategory(@PathVariable Integer id, @Valid @RequestBody CategoryRequestDTO dto) {
        return categoryService.updateCategory(id, dto);
    }


    @Operation(summary = "Delete category", description = "Deletes the category with the specified ID.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    public void deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
    }
}
