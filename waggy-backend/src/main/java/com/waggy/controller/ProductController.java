package com.waggy.controller;

import com.waggy.dto.product.ProductRequestDTO;
import com.waggy.dto.product.ProductResponseDTO;
import com.waggy.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @Operation(summary = "Create product", description = "Creates a new product.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    public ProductResponseDTO createProduct(
            @Valid @RequestPart("product") ProductRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        return productService.saveProductInDb(dto, image);
    }


    @Operation(summary = "Get all products", description = "Returns all available products.")
    @GetMapping
    public List<ProductResponseDTO> getAllProducts() {
        return productService.findAllProducts();
    }


    @Operation(summary = "Get product by ID", description = "Returns the product with the specified ID.")
    @GetMapping("/{id}")
    public ProductResponseDTO getProductById(@PathVariable Integer id) {
        return productService.findProductById(id);
    }


    @Operation(summary = "Get products by category", description = "Returns all products belonging to the specified category.")
    @GetMapping("/category/{categoryId}")
    public List<ProductResponseDTO> getProductByCategory(@PathVariable Integer categoryId) {
        return productService.getProductByCategory(categoryId);
    }


    @Operation(summary = "Update product", description = "Updates an existing product.")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    public ProductResponseDTO updateProduct(
            @PathVariable Integer id,
            @RequestPart("product") @Valid ProductRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        return productService.updateProduct(id, dto, image);
    }


    @Operation(summary = "Delete product", description = "Deletes the product with the specified ID.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    public void deleteProduct(@PathVariable Integer id) throws IOException {
        productService.deleteProduct(id);
    }
}
