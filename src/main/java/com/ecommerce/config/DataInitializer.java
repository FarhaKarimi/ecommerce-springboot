package com.ecommerce.config;

import com.ecommerce.model.Cart;
import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.model.Role;
import com.ecommerce.model.User;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        log.info("Initializing data...");
        
        // Create admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@ecommerce.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            
            User savedAdmin = userRepository.save(admin);
            
            // Create cart for admin
            Cart adminCart = new Cart();
            adminCart.setUser(savedAdmin);
            cartRepository.save(adminCart);
            
            log.info("Admin user created: username=admin, password=admin123");
        }
        
        // Create sample categories
        if (categoryRepository.count() == 0) {
            Category electronics = new Category();
            electronics.setName("Electronics");
            electronics.setDescription("Electronic devices and accessories");
            categoryRepository.save(electronics);
            
            Category clothing = new Category();
            clothing.setName("Clothing");
            clothing.setDescription("Fashion and apparel");
            categoryRepository.save(clothing);
            
            Category books = new Category();
            books.setName("Books");
            books.setDescription("Books and literature");
            categoryRepository.save(books);
            
            log.info("Sample categories created");
            
            // Create sample products
            Product laptop = new Product();
            laptop.setName("MacBook Pro");
            laptop.setDescription("Apple MacBook Pro 14-inch with M3 chip");
            laptop.setPrice(new BigDecimal("1999.99"));
            laptop.setStockQuantity(15);
            laptop.setImageUrl("https://example.com/macbook.jpg");
            laptop.setCategory(electronics);
            laptop.setActive(true);
            productRepository.save(laptop);
            
            Product phone = new Product();
            phone.setName("iPhone 15 Pro");
            phone.setDescription("Latest iPhone with A17 Pro chip");
            phone.setPrice(new BigDecimal("1299.99"));
            phone.setStockQuantity(25);
            phone.setImageUrl("https://example.com/iphone.jpg");
            phone.setCategory(electronics);
            phone.setActive(true);
            productRepository.save(phone);
            
            Product tshirt = new Product();
            tshirt.setName("Cotton T-Shirt");
            tshirt.setDescription("Comfortable cotton t-shirt");
            tshirt.setPrice(new BigDecimal("29.99"));
            tshirt.setStockQuantity(100);
            tshirt.setImageUrl("https://example.com/tshirt.jpg");
            tshirt.setCategory(clothing);
            tshirt.setActive(true);
            productRepository.save(tshirt);
            
            Product book = new Product();
            book.setName("Clean Code");
            book.setDescription("A Handbook of Agile Software Craftsmanship");
            book.setPrice(new BigDecimal("39.99"));
            book.setStockQuantity(50);
            book.setImageUrl("https://example.com/cleancode.jpg");
            book.setCategory(books);
            book.setActive(true);
            productRepository.save(book);
            
            log.info("Sample products created");
        }
        
        log.info("Data initialization completed");
    }
}