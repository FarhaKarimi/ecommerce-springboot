package com.ecommerce.service;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.ProductResponse;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @InjectMocks
    private ProductService productService;
    
    private Category category;
    private Product product;
    private ProductRequest productRequest;
    
    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        
        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("High performance laptop");
        product.setPrice(new BigDecimal("999.99"));
        product.setStockQuantity(10);
        product.setCategory(category);
        product.setActive(true);
        
        productRequest = new ProductRequest();
        productRequest.setName("Laptop");
        productRequest.setDescription("High performance laptop");
        productRequest.setPrice(new BigDecimal("999.99"));
        productRequest.setStockQuantity(10);
        productRequest.setCategoryId(1L);
        productRequest.setActive(true);
    }
    
    @Test
    void testCreateProduct() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        
        ProductResponse response = productService.createProduct(productRequest);
        
        assertNotNull(response);
        assertEquals("Laptop", response.getName());
        assertEquals(new BigDecimal("999.99"), response.getPrice());
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    void testCreateProductWithInvalidCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.createProduct(productRequest);
        });
    }
    
    @Test
    void testGetProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        
        ProductResponse response = productService.getProductById(1L);
        
        assertNotNull(response);
        assertEquals("Laptop", response.getName());
    }
    
    @Test
    void testGetProductByIdNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(1L);
        });
    }
    
    @Test
    void testGetActiveProducts() {
        when(productRepository.findByActiveTrue()).thenReturn(Arrays.asList(product));
        
        List<ProductResponse> products = productService.getActiveProducts();
        
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("Laptop", products.get(0).getName());
    }
    
    @Test
    void testSearchProducts() {
        when(productRepository.searchProducts("laptop")).thenReturn(Arrays.asList(product));
        
        List<ProductResponse> products = productService.searchProducts("laptop");
        
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("Laptop", products.get(0).getName());
    }
    
    @Test
    void testUpdateProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        
        ProductResponse response = productService.updateProduct(1L, productRequest);
        
        assertNotNull(response);
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    void testDeleteProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);
        
        productService.deleteProduct(1L);
        
        verify(productRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testDeleteProductNotFound() {
        when(productRepository.existsById(1L)).thenReturn(false);
        
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.deleteProduct(1L);
        });
    }
}