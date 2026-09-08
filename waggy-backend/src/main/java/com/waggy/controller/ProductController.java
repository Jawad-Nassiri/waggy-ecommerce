package com.waggy.controller;

import com.waggy.dto.product.ProductRequestDTO;
import com.waggy.dto.product.ProductResponseDTO;
import com.waggy.entity.Category;
import com.waggy.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/products")
@RestController
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;


    @PostMapping
    public ProductResponseDTO createProduct(
            @Valid @RequestBody ProductRequestDTO dto,
            @RequestBody Category category) {
        return productService.saveProductInDb(dto, category);
    }

}
