# راهنمای سریع نصب و راه‌اندازی

## پیش‌نیازها

1. Java 17 یا بالاتر
2. Maven 3.6+
3. PostgreSQL 12+

## مراحل نصب

### 1. نصب PostgreSQL


### 2. ایجاد دیتابیس

با استفاده از pgAdmin یا خط فرمان PostgreSQL:

```sql
CREATE DATABASE ecommerce_db;
```

یا می‌توانید از فایل `database-setup.sql` استفاده کنید:

```bash
psql -U postgres -f database-setup.sql
```

### 3. تنظیمات دیتابیس

فایل `src/main/resources/application.properties` را باز کنید و اطلاعات دیتابیس خود را وارد کنید:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### 4. کامپایل و اجرا

در پوشه پروژه:

```bash
# کامپایل پروژه
mvn clean install

# اجرای برنامه
mvn spring-boot:run
```

یا برای ساخت JAR:

```bash
mvn clean package
java -jar target/ecommerce-api-1.0.0.jar
```

### 5. تست API

برنامه روی پورت 8080 اجرا می‌شود:

```bash
curl http://localhost:8080/api/auth/test
```

باید پیام "API is working!" را ببینید.

## حساب کاربری پیش‌فرض

برای دسترسی ادمین:
- **نام کاربری**: admin
- **رمز عبور**: admin123

## تست کامل

```bash
# اجرای تمام تست‌ها
mvn test

# اجرای تست‌های Integration
mvn test -Dtest=EcommerceIntegrationTest

# اجرای تست‌های Unit
mvn test -Dtest=ProductServiceTest
```

## استفاده از API

نمونه‌های کامل استفاده از API در فایل `api-examples.sh` موجود است.

### مثال سریع:

1. **ثبت‌نام مشتری**:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_user",
    "email": "test@example.com",
    "password": "password123"
  }'
```

2. **ورود**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_user",
    "password": "password123"
  }'
```

توکن JWT را ذخیره کنید و در درخواست‌های بعدی استفاده کنید:

```bash
curl -X GET http://localhost:8080/api/cart \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## ساختار پروژه

```
ecommerce-springboot/
├── src/
│   ├── main/
│   │   ├── java/com/ecommerce/
│   │   │   ├── config/          # کانفیگ Security و Data
│   │   │   ├── controller/      # REST Controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── exception/       # مدیریت خطاها
│   │   │   ├── model/           # Entity ها
│   │   │   ├── repository/      # JPA Repositories
│   │   │   ├── security/        # JWT و Security
│   │   │   └── service/         # Business Logic
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/ecommerce/  # تست‌ها
├── pom.xml                       # Maven Configuration
├── README.md                     # مستندات کامل
├── SETUP-FA.md                   # این فایل
├── database-setup.sql            # اسکریپت SQL
└── api-examples.sh               # نمونه APIها
```

## عیب‌یابی

### خطای اتصال به دیتابیس
- مطمئن شوید PostgreSQL در حال اجرا است
- نام کاربری و رمز عبور را چک کنید
- نام دیتابیس را بررسی کنید

### خطای Port در حال استفاده
اگر پورت 8080 در حال استفاده است، در `application.properties` تغییر دهید:
```properties
server.port=8081
```

### خطای Java Version
مطمئن شوید Java 17 نصب است:
```bash
java -version
```

## مشاهده لاگ‌ها

لاگ‌های کامل در کنسول نمایش داده می‌شود. برای تنظیم سطح لاگ:

```properties
logging.level.com.ecommerce=DEBUG
```