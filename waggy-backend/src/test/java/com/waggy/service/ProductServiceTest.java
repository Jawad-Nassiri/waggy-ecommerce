package com.waggy.service;

import com.waggy.dto.category.CategoryResponseDTO;
import com.waggy.dto.product.ProductRequestDTO;
import com.waggy.dto.product.ProductResponseDTO;
import com.waggy.entity.Category;
import com.waggy.entity.Product;
import com.waggy.mapper.ProductMapper;
import com.waggy.repository.CategoryRepository;
import com.waggy.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Product createProduct() {
        Product product = new Product();
        product.setId(1);
        product.setName("pc");
        product.setDescription("description");
        product.setPrice(new BigDecimal("79.99"));
        product.setStock(100);
        product.setImage("image.png");
        product.setCreatedAt(LocalDateTime.parse("2020-12-12T00:00:00"));

        return product;
    }

    private ProductRequestDTO createProductRequestDTO() {
        return new ProductRequestDTO(
                "pc",
                "description",
                new BigDecimal("99.99"),
                100,
                "image.png",
                1
        );
    }

    private ProductResponseDTO createProductResponseDTO() {
        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO(
                1,
                "Dog Food",
                "dog_food"
        );

        return new ProductResponseDTO(
                1,
                "pc",
                "description",
                new BigDecimal("99.99"),
                100,
                "image.png",
                categoryResponseDTO
        );
    }

    @Test
    void saveProductInDb_shouldSaveProduct() {
        ProductRequestDTO dto = createProductRequestDTO();

        Category category = new Category();
        Product product = createProduct();
        Product savedProduct = createProduct();
        CategoryResponseDTO categoryResponse = new CategoryResponseDTO(
                1,
                "Dog Food",
                "dog_food"
        );

        ProductResponseDTO responseDTO = createProductResponseDTO();

        when(categoryRepository.findById(dto.categoryId())).thenReturn(Optional.of(category));
        when(productMapper.toEntity(dto, category)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(productMapper.toDTO(savedProduct)).thenReturn(responseDTO);

        ProductResponseDTO result = productService.saveProductInDb(dto);

        assertEquals(responseDTO, result);
        verify(categoryRepository).findById(dto.categoryId());
        verify(productMapper).toEntity(dto, category);
        verify(productRepository).save(product);
    }

    @Test
    void findAllProducts_shouldFindAllProducts() {
        Product product = createProduct();
        ProductResponseDTO productResponseDTO = createProductResponseDTO();
        List<Product> products = List.of(product);

        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.toDTO(product)).thenReturn(productResponseDTO);

        List<ProductResponseDTO> result = productService.findAllProducts();

        assertEquals(List.of(productResponseDTO), result);
        verify(productRepository).findAll();
        verify(productMapper).toDTO(product);
    }

    @Test
    void findProductById_shouldFindById() {
        Product product = createProduct();
        ProductResponseDTO productResponseDTO = createProductResponseDTO();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productMapper.toDTO(product)).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.findProductById(product.getId());

        assertEquals(productResponseDTO, result);
        verify(productRepository).findById(product.getId());
        verify(productMapper).toDTO(product);
    }

    @Test
    void deleteProduct_shouldDeleteProduct() {
        Product product = createProduct();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        productService.deleteProduct(product.getId());

        verify(productRepository).findById(product.getId());
        verify(productRepository).delete(product);
    }

    @Test
    void updateProduct_shouldUpdateProduct() {
        Product product = createProduct();
        ProductRequestDTO productRequestDTO = createProductRequestDTO();
        ProductResponseDTO productResponseDTO = createProductResponseDTO();
        Category category = new Category();
        category.setId(1);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.updateProduct(product.getId(), productRequestDTO);

        assertEquals(productResponseDTO, result);
        verify(productRepository).findById(product.getId());
        verify(categoryRepository).findById(category.getId());
        verify(productRepository).save(product);
        verify(productMapper).toDTO(product);

    }

}