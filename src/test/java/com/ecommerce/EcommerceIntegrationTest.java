package com.ecommerce;

import com.ecommerce.dto.*;
import com.ecommerce.model.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EcommerceIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private static String adminToken;
    private static String customerToken;
    private static Long categoryId;
    private static Long productId;
    private static Long cartItemId;
    private static Long orderId;
    
    @Test
    @Order(1)
    public void testApiHealth() throws Exception {
        mockMvc.perform(get("/api/auth/test"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("API is working!"));
    }
    
    @Test
    @Order(2)
    public void testRegisterCustomer() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("customer1");
        request.setEmail("customer1@test.com");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhone("1234567890");
        request.setAddress("123 Main St");
        
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.username").value("customer1"))
            .andExpect(jsonPath("$.email").value("customer1@test.com"))
            .andReturn();
        
        AuthResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(), 
            AuthResponse.class
        );
        customerToken = response.getToken();
    }
    
    @Test
    @Order(3)
    public void testRegisterAdmin() throws Exception {
        // First register as customer
        RegisterRequest request = new RegisterRequest();
        request.setUsername("admin1");
        request.setEmail("admin1@test.com");
        request.setPassword("admin123");
        request.setFirstName("Admin");
        request.setLastName("User");
        
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn();
        
        AuthResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(), 
            AuthResponse.class
        );
        adminToken = response.getToken();
        
        // Note: In real app, you'd need to manually set admin role in DB
        // For this test, we'll use the customer token for admin operations
    }
    
    @Test
    @Order(4)
    public void testLogin() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("customer1");
        request.setPassword("password123");
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.username").value("customer1"));
    }
    
    @Test
    @Order(5)
    public void testCreateCategory() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("Electronics");
        request.setDescription("Electronic devices and accessories");
        
        MvcResult result = mockMvc.perform(post("/api/admin/categories")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Electronics"))
            .andReturn();
        
        CategoryResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(), 
            CategoryResponse.class
        );
        categoryId = response.getId();
    }
    
    @Test
    @Order(6)
    public void testGetAllCategories() throws Exception {
        mockMvc.perform(get("/api/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Electronics"));
    }
    
    @Test
    @Order(7)
    public void testCreateProduct() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Laptop");
        request.setDescription("High performance laptop");
        request.setPrice(new BigDecimal("999.99"));
        request.setStockQuantity(10);
        request.setImageUrl("http://example.com/laptop.jpg");
        request.setCategoryId(categoryId);
        request.setActive(true);
        
        MvcResult result = mockMvc.perform(post("/api/admin/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Laptop"))
            .andExpect(jsonPath("$.price").value(999.99))
            .andReturn();
        
        ProductResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(), 
            ProductResponse.class
        );
        productId = response.getId();
    }
    
    @Test
    @Order(8)
    public void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Laptop"));
    }
    
    @Test
    @Order(9)
    public void testSearchProducts() throws Exception {
        mockMvc.perform(get("/api/products/search")
                .param("keyword", "laptop"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Laptop"));
    }
    
    @Test
    @Order(10)
    public void testAddToCart() throws Exception {
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(productId);
        request.setQuantity(2);
        
        MvcResult result = mockMvc.perform(post("/api/cart/add")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items[0].productName").value("Laptop"))
            .andExpect(jsonPath("$.items[0].quantity").value(2))
            .andReturn();
        
        CartResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(), 
            CartResponse.class
        );
        cartItemId = response.getItems().get(0).getId();
    }
    
    @Test
    @Order(11)
    public void testGetCart() throws Exception {
        mockMvc.perform(get("/api/cart")
                .header("Authorization", "Bearer " + customerToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items[0].productName").value("Laptop"))
            .andExpect(jsonPath("$.totalItems").value(1));
    }
    
    @Test
    @Order(12)
    public void testUpdateCartItem() throws Exception {
        mockMvc.perform(put("/api/cart/items/" + cartItemId)
                .header("Authorization", "Bearer " + customerToken)
                .param("quantity", "3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items[0].quantity").value(3));
    }
    
    @Test
    @Order(13)
    public void testCreateOrder() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setShippingAddress("123 Main St, City, Country");
        request.setPhoneNumber("1234567890");
        request.setNotes("Please deliver in the morning");
        
        MvcResult result = mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.items[0].productName").value("Laptop"))
            .andReturn();
        
        OrderResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(), 
            OrderResponse.class
        );
        orderId = response.getId();
    }
    
    @Test
    @Order(14)
    public void testGetUserOrders() throws Exception {
        mockMvc.perform(get("/api/orders")
                .header("Authorization", "Bearer " + customerToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].status").value("PENDING"));
    }
    
    @Test
    @Order(15)
    public void testCartEmptyAfterOrder() throws Exception {
        mockMvc.perform(get("/api/cart")
                .header("Authorization", "Bearer " + customerToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalItems").value(0));
    }
    
    @Test
    @Order(16)
    public void testDuplicateUsernameRegistration() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("customer1");
        request.setEmail("different@test.com");
        request.setPassword("password123");
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    @Order(17)
    public void testInvalidLogin() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("customer1");
        request.setPassword("wrongpassword");
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    @Order(18)
    public void testAddToCartInsufficientStock() throws Exception {
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(productId);
        request.setQuantity(100); // More than available stock
        
        mockMvc.perform(post("/api/cart/add")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}