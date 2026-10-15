package com.waggy.controller;

import com.waggy.dto.product.ProductRequestDTO;
import com.waggy.dto.product.ProductResponseDTO;
import com.waggy.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestPart;
import java.io.IOException;
import java.util.List;

@RequestMapping("/products")
@RestController
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponseDTO createProduct(
            @Valid @RequestPart("product") ProductRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        return productService.saveProductInDb(dto, image);
    }

    @GetMapping
    public List<ProductResponseDTO> getAllProducts() {
        return productService.findAllProducts();
    }

    @GetMapping("/{id}")
    public ProductResponseDTO getProductById(@PathVariable Integer id) {
        return productService.findProductById(id);
    }

    @GetMapping("/category/{categoryId}")
    public List<ProductResponseDTO> getProductByCategory(@PathVariable Integer categoryId) {
        return productService.getProductByCategory(categoryId);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponseDTO updateProduct(
            @PathVariable Integer id,
            @RequestPart("product") @Valid ProductRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        return productService.updateProduct(id, dto, image);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Integer id) throws IOException {
        productService.deleteProduct(id);
    }
}
