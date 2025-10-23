# API Testing Collection
# Use with tools like cURL, Postman, or HTTPie

# Base URL
BASE_URL="http://localhost:8080"

# 1. Test API Health
curl -X GET "${BASE_URL}/api/auth/test"

# 2. Register Customer
curl -X POST "${BASE_URL}/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "customer1",
    "email": "customer1@test.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "1234567890",
    "address": "123 Main St"
  }'

# 3. Login Admin (use default admin credentials)
curl -X POST "${BASE_URL}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'

# Save the token from response
ADMIN_TOKEN="YOUR_TOKEN_HERE"

# 4. Get All Categories
curl -X GET "${BASE_URL}/api/categories"

# 5. Create Category (Admin)
curl -X POST "${BASE_URL}/api/admin/categories" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Electronics",
    "description": "Electronic devices and accessories"
  }'

# 6. Create Product (Admin)
curl -X POST "${BASE_URL}/api/admin/products" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High performance laptop",
    "price": 999.99,
    "stockQuantity": 10,
    "imageUrl": "http://example.com/laptop.jpg",
    "categoryId": 1,
    "active": true
  }'

# 7. Get All Products
curl -X GET "${BASE_URL}/api/products"

# 8. Search Products
curl -X GET "${BASE_URL}/api/products/search?keyword=laptop"

# Login Customer
curl -X POST "${BASE_URL}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "customer1",
    "password": "password123"
  }'

# Save customer token
CUSTOMER_TOKEN="YOUR_CUSTOMER_TOKEN_HERE"

# 9. Add to Cart
curl -X POST "${BASE_URL}/api/cart/add" \
  -H "Authorization: Bearer ${CUSTOMER_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 2
  }'

# 10. Get Cart
curl -X GET "${BASE_URL}/api/cart" \
  -H "Authorization: Bearer ${CUSTOMER_TOKEN}"

# 11. Create Order
curl -X POST "${BASE_URL}/api/orders" \
  -H "Authorization: Bearer ${CUSTOMER_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "shippingAddress": "123 Main St, City, Country",
    "phoneNumber": "1234567890",
    "notes": "Please deliver in the morning"
  }'

# 12. Get User Orders
curl -X GET "${BASE_URL}/api/orders" \
  -H "Authorization: Bearer ${CUSTOMER_TOKEN}"

# 13. Get All Orders (Admin)
curl -X GET "${BASE_URL}/api/orders/admin/all" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}"

# 14. Update Order Status (Admin)
curl -X PUT "${BASE_URL}/api/orders/admin/1/status?status=PROCESSING" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}"