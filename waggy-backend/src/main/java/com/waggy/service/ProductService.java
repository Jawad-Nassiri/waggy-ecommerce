package com.waggy.service;

import com.waggy.dto.product.ProductRequestDTO;
import com.waggy.dto.product.ProductResponseDTO;
import com.waggy.entity.Category;
import com.waggy.entity.Product;
import com.waggy.exception.CategoryNotFoundException;
import com.waggy.exception.ProductNotFoundException;
import com.waggy.mapper.ProductMapper;
import com.waggy.repository.CategoryRepository;
import com.waggy.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;


    public ProductResponseDTO saveProductInDb(ProductRequestDTO dto, Category category) {
        Product product = productMapper.toEntity(dto, category);
        Product savedProduct = productRepository.save(product);

        return productMapper.toDTO(savedProduct);
    }


    public List<ProductResponseDTO> findAllProducts() {
        return productRepository.findAll().stream().map(productMapper::toDTO).toList();
    }


    public ProductResponseDTO findProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found !"));
        return productMapper.toDTO(product);
    }


    public void deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found !"));
        productRepository.delete(product);
    }

    public ProductResponseDTO updateProduct(Integer id, ProductRequestDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found !"));

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found !"));

        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setStock(dto.stock());
        product.setImage(dto.image());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);

        return productMapper.toDTO(updatedProduct);
    }


}
