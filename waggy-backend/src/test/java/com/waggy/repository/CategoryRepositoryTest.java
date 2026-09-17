package com.waggy.repository;


import com.waggy.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    private Category createCategory() {
        Category category = new Category();
        category.setName("Dog Food");
        category.setSlug("dog_food");
        return category;
    }

    @Test
    void existsByName_shouldReturnTrue_whenNameExist() {
        Category category = createCategory();
        categoryRepository.save(category);

        Boolean result = categoryRepository.existsByName("Dog Food");
        assertTrue(result);
    }

    @Test
    void existsByName_shouldReturnFalse_whenNameDoesNotExist() {
        Boolean result = categoryRepository.existsByName("unknown");
        assertFalse(result);
    }

    @Test
    void existsBySlug_shouldReturnTrue_whenSlugExist() {
        Category category = createCategory();
        categoryRepository.save(category);

        Boolean result = categoryRepository.existsBySlug("dog_food");
        assertTrue(result);
    }

    @Test
    void existsBySlug_shouldReturnFalse_whenSlugDoesNotExist() {
        Boolean result = categoryRepository.existsBySlug("unknown");
        assertFalse(result);
    }
}
