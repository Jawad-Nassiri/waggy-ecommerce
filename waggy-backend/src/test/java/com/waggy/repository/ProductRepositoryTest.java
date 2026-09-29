package com.waggy.repository;

import com.waggy.entity.Category;
import com.waggy.entity.Product;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldFindProductsByCategoryId() {
        Category category = new Category();
        category.setName("Pet Clothing");
        category.setSlug("pet-clothing");

        entityManager.persist(category);
        entityManager.flush();

        Product product = new Product();
        product.setName("Hoodie");
        product.setPrice(new BigDecimal("20.00"));
        product.setStock(10);
        product.setImage("products/clothing/hoodie1.jpg");
        product.setCategory(category);

        entityManager.persist(product);

        entityManager.flush();

        List<Product> products = productRepository.findByCategoryId(category.getId());

        assertFalse(products.isEmpty());
        assertEquals(category.getId(), products.get(0).getCategory().getId());
    }

}
