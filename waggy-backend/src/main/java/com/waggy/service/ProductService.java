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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;

    public ProductResponseDTO saveProductInDb(ProductRequestDTO dto, MultipartFile image) throws IOException {
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image must not be empty");
        }

        String imagePath = imageService.saveImage(image, category.getSlug());

        ProductRequestDTO productDTO = new ProductRequestDTO(
                dto.name(),
                dto.description(),
                dto.price(),
                dto.stock(),
                imagePath,
                dto.categoryId()
        );

        Product product = productMapper.toEntity(productDTO, category);
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

    public List<ProductResponseDTO> getProductByCategory(Integer categoryId) {
        return productRepository.findByCategoryId(categoryId).stream().map(productMapper::toDTO).toList();
    }

    public void deleteProduct(Integer id) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found !"));

        imageService.deleteImage(product.getImage());

        productRepository.delete(product);
    }

    public ProductResponseDTO updateProduct(Integer id, ProductRequestDTO dto, MultipartFile image) throws IOException, IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found !"));

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found !"));

        System.out.println("SLUG = " + category.getSlug());


        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setStock(dto.stock());
        product.setCategory(category);

        if (image != null && !image.isEmpty()) {
            if (product.getImage() != null) {
                imageService.deleteImage(product.getImage());
            }
            String imagePath = imageService.saveImage(image, category.getSlug());
            product.setImage(imagePath);
        } else if (product.getImage() != null) {
            String fileName = product.getImage().substring(
                    product.getImage().lastIndexOf('/') + 1
            );
            product.setImage(category.getSlug().toLowerCase() + "/" + fileName);
        }

        Product updatedProduct = productRepository.save(product);

        return productMapper.toDTO(updatedProduct);
    }
}
