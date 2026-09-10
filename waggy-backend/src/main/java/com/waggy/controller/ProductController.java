package com.waggy.controller;

import com.waggy.dto.product.ProductRequestDTO;
import com.waggy.dto.product.ProductResponseDTO;
import com.waggy.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/products")
@RestController
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ProductResponseDTO createProduct(@Valid @RequestBody ProductRequestDTO dto) {
        return productService.saveProductInDb(dto);
    }


    @GetMapping
    public List<ProductResponseDTO> getAllProducts() {
        return productService.findAllProducts();
    }


    @GetMapping("/{id}")
    public ProductResponseDTO getProductById(@PathVariable Integer id) {
        return productService.findProductById(id);
    }


    @PutMapping("/{id}")
    public ProductResponseDTO updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody ProductRequestDTO dto
    ) {
        return productService.updateProduct(id, dto);

    }


    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
    }
}
