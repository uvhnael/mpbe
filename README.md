# AI-Based Meal Planner App - Backend (MPBE)

## Giới thiệu

Backend API cho ứng dụng lập kế hoạch bữa ăn thông minh sử dụng AI Gemini để đề xuất thực đơn phù hợp với sở thích, mục tiêu sức khỏe và ngân sách của người dùng.

## Công nghệ sử dụng

- **Framework**: Spring Boot 4.0.0
- **Language**: Java 17
- **Database**: MySQL 8+ (Production/Development), H2 (Testing)
- **AI Integration**: Google Gemini AI API
- **Authentication**: Spring Security + JWT (RS512 Algorithm)
- **Build Tool**: Maven 3.9+
- **Documentation**: Swagger/OpenAPI (springdoc)
- **Logging**: Spring AOP + AspectJ
- **Testing**: JUnit 5, Mockito, H2 In-Memory Database

## Tính năng chính

### 1. Authentication & Security ✅ 

- Đăng ký, đăng nhập với JWT (RS512 Algorithm)
- Refresh Token mechanism với expiry tracking
- Password encryption với BCrypt
- Role-based access control
- CORS configuration
- Security headers (X-Frame-Options, X-Content-Type-Options, X-XSS-Protection)

### 2. Quản lý người dùng ✅

- Quản lý profile (chiều cao, cân nặng, mục tiêu sức khỏe)
- Lưu sở thích ăn uống, dị ứng thực phẩm
- Cập nhật thông tin cá nhân
- Validation đầy đủ cho input data

### 3. AI Meal Planning ✅

- Tạo kế hoạch bữa ăn tự động với Gemini AI
- Đề xuất món ăn dựa trên:
  - Mục tiêu (giảm cân, tăng cơ, duy trì sức khỏe)
  - Sở thích cá nhân
  - Dị ứng thực phẩm
  - Ngân sách
- Parse và lưu AI response vào database
- Tính toán dinh dưỡng chi tiết với NutritionService

### 4. Quản lý công thức món ăn ✅

- CRUD operations cho recipes
- Tìm kiếm và lọc recipes với pagination
- Lưu recipes yêu thích (UserRecipeFavorite)
- Tính toán dinh dưỡng tự động từ ingredients
- Upload và quản lý hình ảnh món ăn

### 5. Shopping List ✅

- Tự động generate shopping list từ meal plan
- Nhóm ingredients theo category
- Aggregate quantities cho cùng ingredient
- Toggle item status (checked/unchecked)
- Delete individual items
- Tính toán tổng chi phí

### 6. Logging & Monitoring ✅

- Performance logging cho Controllers
- Detailed logging cho Services
- Query logging cho Repositories
- Audit trail cho data modifications
- Exception tracking với GlobalExceptionHandler

## Cấu trúc dự án

```
mpbe/
├── src/
│   ├── main/
│   │   ├── java/org/uvhnael/mpbe/
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java          # JWT + Security configuration
│   │   │   │   ├── OpenAPIConfig.java           # Swagger/OpenAPI setup
│   │   │   │   ├── JacksonConfig.java           # JSON serialization config
│   │   │   │   ├── CorsConfig.java              # CORS configuration
│   │   │   │   └── AopConfig.java               # AspectJ auto-proxying
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java          # Register, Login, Refresh Token
│   │   │   │   ├── UserController.java          # User profile management
│   │   │   │   ├── MealPlanController.java      # AI meal plan generation
│   │   │   │   ├── RecipeController.java        # Recipe CRUD + search
│   │   │   │   └── ShoppingListController.java  # Shopping list management
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java                    # Authentication logic
│   │   │   │   ├── UserService.java                    # User operations
│   │   │   │   ├── UserProfileService.java             # Profile management
│   │   │   │   ├── MealPlanService.java                # Meal plan CRUD
│   │   │   │   ├── RecipeService.java                  # Recipe operations
│   │   │   │   ├── GeminiAIService.java                # AI integration
│   │   │   │   ├── AIMealPlanParserService.java        # Parse AI responses
│   │   │   │   ├── NutritionService.java               # Nutrition calculations
│   │   │   │   ├── ShoppingListGeneratorService.java   # Generate shopping lists
│   │   │   │   ├── ShoppingListItemService.java        # Shopping list items
│   │   │   │   ├── UserRecipeFavoriteService.java      # Favorite recipes
│   │   │   │   └── RefreshTokenService.java            # Token management
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── UserProfileRepository.java
│   │   │   │   ├── MealPlanRepository.java
│   │   │   │   ├── MealPlanItemRepository.java
│   │   │   │   ├── RecipeRepository.java
│   │   │   │   ├── IngredientRepository.java
│   │   │   │   ├── ShoppingListRepository.java
│   │   │   │   ├── ShoppingListItemRepository.java
│   │   │   │   ├── UserRecipeFavoriteRepository.java
│   │   │   │   └── RefreshTokenRepository.java
│   │   │   ├── model/
│   │   │   │   ├── User.java                    # @CreationTimestamp, @UpdateTimestamp
│   │   │   │   ├── UserProfile.java             # Health & dietary info
│   │   │   │   ├── MealPlan.java                # Meal plan entity
│   │   │   │   ├── MealPlanItem.java            # Individual meals
│   │   │   │   ├── Recipe.java                  # Recipe with ingredients
│   │   │   │   ├── Ingredient.java              # Ingredient with nutrition
│   │   │   │   ├── ShoppingList.java            # Shopping list entity
│   │   │   │   ├── ShoppingListItem.java        # Shopping items
│   │   │   │   ├── UserRecipeFavorite.java      # Favorite recipes
│   │   │   │   └── RefreshToken.java            # JWT refresh tokens
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   ├── LoginRequest.java        # @Valid, @Email, @NotBlank
│   │   │   │   │   ├── RegisterRequest.java     # @Valid with constraints
│   │   │   │   │   ├── MealPlanRequest.java     # @Min, @Max validation
│   │   │   │   │   ├── UserProfileRequest.java  # @DecimalMin validation
│   │   │   │   │   └── PaginationRequest.java   # Page, size, sort
│   │   │   │   └── response/
│   │   │   │       ├── AuthResponse.java        # JWT tokens
│   │   │   │       ├── ApiResponse.java         # Generic API response
│   │   │   │       └── PagedResponse.java       # Paginated data
│   │   │   ├── security/
│   │   │   │   ├── JwtTokenProvider.java        # RS512 JWT generation
│   │   │   │   ├── JwtAuthenticationFilter.java # Token validation filter
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   ├── aspect/
│   │   │   │   ├── LoggingAspect.java           # Performance monitoring
│   │   │   │   └── AuditLoggingAspect.java      # Audit trail
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java  # Centralized exception handling
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── UnauthorizedException.java
│   │   │   │   └── InvalidTokenException.java
│   │   │   └── MpbeApplication.java
│   │   └── resources/
│   │       ├── application.properties           # Main config, profile=dev
│   │       ├── application-dev.properties       # Development settings
│   │       ├── application-prod.properties      # Production with env vars
│   │       ├── private_key.pem                  # JWT RS512 private key
│   │       └── public_key.pem                   # JWT RS512 public key
│   └── test/
│       ├── java/org/uvhnael/mpbe/
│       │   ├── MpbeApplicationTests.java        # Context load test
│       │   ├── security/
│       │   │   └── JwtTokenProviderTest.java    # JWT tests (5 tests)
│       │   └── service/
│       │       ├── AuthServiceTest.java         # Auth tests (5 tests)
│       │       └── UserRecipeFavoriteServiceTest.java (5 tests)
│       └── resources/
│           └── application-test.properties      # H2 in-memory config
├── pom.xml
├── README.md
├── CRITICAL_FIXES.md                            # Documentation of fixes
└── ADDITIONAL_FIXES.md                          # Additional improvements
```

## Database Schema

### Users Table

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    created_at DATETIME(6),                    -- @CreationTimestamp
    updated_at DATETIME(6),                    -- @UpdateTimestamp
    INDEX idx_email (email)                    -- Performance optimization
);
```

### User Profiles Table

```sql
CREATE TABLE user_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE NOT NULL,
    age INT,
    gender VARCHAR(20),
    height DECIMAL(5,2),
    weight DECIMAL(5,2),
    goal VARCHAR(50),                          -- weight_loss, muscle_gain, maintain
    activity_level VARCHAR(50),
    dietary_preference VARCHAR(100),           -- vegetarian, vegan, keto, etc.
    allergies TEXT,
    budget_per_meal DECIMAL(10,2),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
);
```

### Recipes Table

```sql
CREATE TABLE recipes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    cuisine_type VARCHAR(100),
    meal_type VARCHAR(50),                     -- breakfast, lunch, dinner, snack
    prep_time INT,
    cook_time INT,
    servings INT,
    difficulty VARCHAR(50),
    image_url VARCHAR(500),
    instructions TEXT,
    created_by BIGINT,
    created_at DATETIME(6),                    -- @CreationTimestamp
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_meal_type (meal_type),
    INDEX idx_cuisine_type (cuisine_type),
    INDEX idx_created_by (created_by)
);
```

### Ingredients Table

```sql
CREATE TABLE ingredients (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipe_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    quantity DECIMAL(10,2),
    unit VARCHAR(50),
    calories DECIMAL(10,2),
    protein DECIMAL(10,2),
    carbs DECIMAL(10,2),
    fat DECIMAL(10,2),
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    INDEX idx_recipe_id (recipe_id),
    INDEX idx_name (name)
);
```

### Meal Plans Table

```sql
CREATE TABLE meal_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    start_date DATE,
    end_date DATE,
    total_calories INT,
    status VARCHAR(50),                        -- active, completed, draft
    created_at DATETIME(6),                    -- @CreationTimestamp
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_date_range (start_date, end_date)
);
```

### Meal Plan Items Table

```sql
CREATE TABLE meal_plan_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    meal_plan_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    day_of_week INT,
    meal_type VARCHAR(50),
    FOREIGN KEY (meal_plan_id) REFERENCES meal_plans(id) ON DELETE CASCADE,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id),
    INDEX idx_meal_plan_id (meal_plan_id),
    INDEX idx_recipe_id (recipe_id)
);
```

### Shopping Lists Table

```sql
CREATE TABLE shopping_lists (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    meal_plan_id BIGINT,
    user_id BIGINT NOT NULL,
    created_at DATETIME(6),                    -- @CreationTimestamp
    FOREIGN KEY (meal_plan_id) REFERENCES meal_plans(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_id (user_id),
    INDEX idx_meal_plan_id (meal_plan_id)
);
```

### Shopping List Items Table

```sql
CREATE TABLE shopping_list_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    shopping_list_id BIGINT NOT NULL,
    ingredient_name VARCHAR(255) NOT NULL,
    quantity DECIMAL(10,2),
    unit VARCHAR(50),
    category VARCHAR(100),
    estimated_cost DECIMAL(10,2),
    is_checked BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (shopping_list_id) REFERENCES shopping_lists(id) ON DELETE CASCADE,
    INDEX idx_shopping_list_id (shopping_list_id)
);
```

### User Recipe Favorites Table

```sql
CREATE TABLE user_recipe_favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    created_at DATETIME(6),                    -- @CreationTimestamp
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_recipe (user_id, recipe_id),
    INDEX idx_user_id (user_id),
    INDEX idx_recipe_id (recipe_id)
);
```

### Refresh Tokens Table

```sql
CREATE TABLE refresh_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) UNIQUE NOT NULL,
    expiry_date DATETIME(6) NOT NULL,
    created_at DATETIME(6),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_token (token),
    INDEX idx_user_id (user_id),
    INDEX idx_expiry_date (expiry_date)
);
```

## API Endpoints

### Authentication (`/api/auth`)

```http
POST   /api/auth/register           # Đăng ký tài khoản mới
       Body: { email, password, fullName }

POST   /api/auth/login              # Đăng nhập
       Body: { email, password }
       Response: { accessToken, refreshToken, tokenType, expiresIn }

POST   /api/auth/refresh            # Refresh access token
       Body: { refreshToken }
       Response: { accessToken, refreshToken, tokenType, expiresIn }

POST   /api/auth/logout             # Đăng xuất (xóa refresh token)
       Params: userId
```

### User Management (`/api/users`)

```http
GET    /api/users/profile/{userId}  # Lấy thông tin profile
       Response: UserProfile entity

POST   /api/users/profile/{userId}  # Tạo profile mới
       Body: UserProfileRequest (age, gender, height, weight, etc.)

PUT    /api/users/profile/{userId}  # Cập nhật profile
       Body: UserProfileRequest (with validation)

PUT    /api/users/preferences/{userId}  # Cập nhật sở thích ăn uống
       Body: { dietaryPreference, allergies, budgetPerMeal }
```

### Meal Planning - AI-Powered (`/api/meal-plans`)

```http
POST   /api/meal-plans/generate     # Tạo meal plan tự động với Gemini AI
       Params: userId
       Body: { days: 1-30, startDate }
       Response: Complete meal plan with recipes

GET    /api/meal-plans/user/{userId}  # Lấy danh sách meal plans
       Params: page, size, sort
       Response: Paginated meal plans

GET    /api/meal-plans/{id}         # Lấy chi tiết meal plan
       Response: MealPlan with items and recipes

PUT    /api/meal-plans/{id}         # Cập nhật meal plan
       Body: MealPlan entity

DELETE /api/meal-plans/{id}         # Xóa meal plan
```

### Recipes (`/api/recipes`)

```http
GET    /api/recipes                 # Lấy danh sách recipes (paginated)
       Params: page, size, sortBy, sortDirection
       Response: PagedResponse<Recipe>

GET    /api/recipes/{id}            # Lấy chi tiết recipe
       Response: Recipe with ingredients

POST   /api/recipes                 # Tạo recipe mới
       Body: Recipe entity with ingredients

PUT    /api/recipes/{id}            # Cập nhật recipe
       Body: Recipe entity

DELETE /api/recipes/{id}            # Xóa recipe

GET    /api/recipes/{id}/nutrition  # Tính toán nutrition info
       Response: { totalCalories, totalProtein, totalCarbs, totalFat }

POST   /api/recipes/favorites       # Lưu recipe yêu thích
       Params: userId, recipeId

DELETE /api/recipes/favorites       # Xóa recipe khỏi favorites
       Params: userId, recipeId

GET    /api/recipes/favorites/{userId}  # Lấy danh sách favorites
       Response: List<Recipe>
```

### Shopping Lists (`/api/shopping-lists`)

```http
POST   /api/shopping-lists/generate-from-meal-plan  # Generate từ meal plan
       Params: mealPlanId, userId
       Response: ShoppingList with aggregated items

GET    /api/shopping-lists/{id}    # Lấy chi tiết shopping list
       Response: ShoppingList with items

GET    /api/shopping-lists/user/{userId}  # Lấy shopping lists của user
       Response: List<ShoppingList>

POST   /api/shopping-lists/{id}/items  # Thêm item mới
       Body: ShoppingListItem

PUT    /api/shopping-lists/{id}/items/{itemId}  # Cập nhật item
       Body: ShoppingListItem

DELETE /api/shopping-lists/items/{itemId}  # Xóa item

PUT    /api/shopping-lists/items/{itemId}/toggle  # Toggle checked status
       Response: Updated ShoppingListItem
```

### API Documentation

```http
GET    /swagger-ui/index.html       # Swagger UI (dev only)
GET    /v3/api-docs                 # OpenAPI JSON spec
```

## Cấu hình

### Profiles

Dự án sử dụng 3 profiles:

- **dev** (default): Development với MySQL
- **prod**: Production với environment variables
- **test**: Testing với H2 in-memory database

### application.properties (Main)

```properties
# Application Name
spring.application.name=mpbe

# Active Profile
spring.profiles.active=dev

# Database Configuration (overridden by profiles)
spring.datasource.url=jdbc:mysql://192.168.2.149:3306/meal_planner?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# JWT Configuration (RS512 Algorithm)
jwt.access-token-expiration=3600000        # 1 hour
jwt.refresh-token-expiration=2592000000    # 30 days
jwt.private-key=classpath:private_key.pem
jwt.public-key=classpath:public_key.pem

# Gemini AI
gemini.api.key=your_gemini_api_key
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/

# Logging
logging.level.org.springframework.web=INFO
logging.level.org.uvhnael.mpbe=INFO
logging.level.org.hibernate.SQL=DEBUG
```

### application-dev.properties (Development)

```properties
# Development Server
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://192.168.2.149:3306/meal_planner?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=123456

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Logging - Debug level for development
logging.level.org.springframework.web=DEBUG
logging.level.org.uvhnael.mpbe=DEBUG
logging.level.org.hibernate.SQL=DEBUG

# CORS - Permissive for development
cors.allowed-origins=http://localhost:3000,http://localhost:4200,http://localhost:5173

# Swagger/OpenAPI - Enabled in dev
springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.path=/swagger-ui.html
```

### application-prod.properties (Production)

```properties
# Production Server
server.port=${PORT:8080}

# Database - Use environment variables
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JPA - Validate schema only
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Security
jwt.access-token-expiration=1800000        # 30 minutes
jwt.refresh-token-expiration=604800000     # 7 days
jwt.private-key=${JWT_PRIVATE_KEY_PATH}
jwt.public-key=${JWT_PUBLIC_KEY_PATH}

# Gemini AI
gemini.api.key=${GEMINI_API_KEY}

# Logging - Warn level for production
logging.level.org.springframework.web=WARN
logging.level.org.uvhnael.mpbe=INFO
logging.level.org.hibernate.SQL=WARN

# CORS - Restrictive for production
cors.allowed-origins=${ALLOWED_ORIGINS}

# Swagger - Disabled in production
springdoc.api-docs.enabled=false
springdoc.swagger-ui.enabled=false

# Security Headers
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.secure=true
```

### application-test.properties (Testing)

```properties
# H2 In-Memory Database for Testing
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console
spring.h2.console.enabled=true

# JPA
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect

# Disable Swagger in tests
springdoc.api-docs.enabled=false
springdoc.swagger-ui.enabled=false
```

## Cài đặt và chạy

### Yêu cầu hệ thống

- **Java 17** trở lên
- **Maven 3.9+**
- **MySQL 8+** (cho development/production)
- **Google Gemini API Key**

### Các bước cài đặt

#### 1. Clone repository

```bash
git clone <repository-url>
cd mpbe
```

#### 2. Cấu hình database MySQL

```bash
# Tạo database
mysql -u root -p -e "CREATE DATABASE meal_planner CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"

# Hoặc database sẽ tự động tạo nếu dùng createDatabaseIfNotExist=true
```

#### 3. Generate JWT Keys (RS512)

```bash
# Generate private key
openssl genpkey -algorithm RSA -out src/main/resources/private_key.pem -pkeyopt rsa_keygen_bits:2048

# Generate public key
openssl rsa -pubout -in src/main/resources/private_key.pem -out src/main/resources/public_key.pem
```

#### 4. Cấu hình application-dev.properties

Chỉnh sửa `src/main/resources/application-dev.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/meal_planner?createDatabaseIfNotExist=true
spring.datasource.username=your_username
spring.datasource.password=your_password

# Gemini AI
gemini.api.key=your_gemini_api_key_here
```

#### 5. Lấy Gemini API Key

1. Truy cập: https://makersuite.google.com/app/apikey
2. Tạo API key mới
3. Thêm vào `application-dev.properties`

#### 6. Build project

```bash
# Compile và package
mvn clean install

# Hoặc skip tests nếu cần
mvn clean install -DskipTests
```

#### 7. Chạy ứng dụng

```bash
# Chạy với Maven
mvn spring-boot:run

# Hoặc chạy với profile cụ thể
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Hoặc chạy JAR file
java -jar target/mpbe-0.0.1-SNAPSHOT.jar
```

#### 8. Kiểm tra API

```bash
# Kiểm tra Swagger UI
http://localhost:8080/swagger-ui/index.html

# Test register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123","fullName":"Test User"}'

# Test login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

## Tích hợp Gemini AI

### Overview

Dự án sử dụng **Google Gemini AI** để generate meal plans dựa trên user profile và preferences.

### GeminiAIService.java - Implementation

```java
@Service
public class GeminiAIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Generate meal plan từ Gemini AI
     * @param profile User profile với dietary preferences
     * @param days Số ngày cần generate (1-30)
     * @return AI response text (JSON format)
     */
    public String generateMealPlan(UserProfile profile, int days) {
        String prompt = buildMealPlanPrompt(profile, days);

        // Build request
        Map<String, Object> request = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", prompt)
                ))
            )
        );

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            // Call Gemini API
            String fullUrl = apiUrl + "gemini-1.5-flash:generateContent";
            ResponseEntity<String> response = restTemplate.postForEntity(
                fullUrl, entity, String.class
            );

            // Extract text from response
            return extractTextFromGeminiResponse(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate meal plan from Gemini AI", e);
        }
    }

    /**
     * Build prompt cho Gemini AI với user preferences
     */
    private String buildMealPlanPrompt(UserProfile profile, int days) {
        BigDecimal dailyCalories = calculateDailyCalories(profile);

        return String.format("""
            Tạo kế hoạch bữa ăn %d ngày cho người dùng với thông tin sau:
            - Mục tiêu: %s
            - Sở thích ăn uống: %s
            - Dị ứng: %s
            - Ngân sách mỗi bữa: %.0f VNĐ
            - Calories mỗi ngày: %.0f

            Yêu cầu format JSON như sau:
            {
              "days": [
                {
                  "dayNumber": 1,
                  "date": "YYYY-MM-DD",
                  "meals": [
                    {
                      "mealType": "breakfast|lunch|dinner|snack",
                      "recipeName": "Tên món ăn",
                      "description": "Mô tả ngắn",
                      "ingredients": [
                        {
                          "name": "Tên nguyên liệu",
                          "quantity": 100,
                          "unit": "g|ml|tbsp|cup",
                          "calories": 50,
                          "protein": 5,
                          "carbs": 10,
                          "fat": 2
                        }
                      ],
                      "instructions": "Các bước nấu",
                      "prepTime": 15,
                      "cookTime": 20,
                      "servings": 2
                    }
                  ]
                }
              ]
            }

            Chỉ trả về JSON, không thêm text khác.
            """,
            days,
            profile.getGoal() != null ? profile.getGoal() : "maintain",
            profile.getDietaryPreference() != null ? profile.getDietaryPreference() : "no preference",
            profile.getAllergies() != null ? profile.getAllergies() : "none",
            profile.getBudgetPerMeal() != null ? profile.getBudgetPerMeal().doubleValue() : 50000,
            dailyCalories.doubleValue()
        );
    }

    /**
     * Calculate daily calories dựa trên BMR và activity level
     */
    private BigDecimal calculateDailyCalories(UserProfile profile) {
        if (profile.getWeight() == null || profile.getHeight() == null || profile.getAge() == null) {
            return BigDecimal.valueOf(2000); // Default
        }

        // Mifflin-St Jeor Equation
        double bmr;
        if ("male".equalsIgnoreCase(profile.getGender())) {
            bmr = 10 * profile.getWeight().doubleValue()
                + 6.25 * profile.getHeight().doubleValue()
                - 5 * profile.getAge() + 5;
        } else {
            bmr = 10 * profile.getWeight().doubleValue()
                + 6.25 * profile.getHeight().doubleValue()
                - 5 * profile.getAge() - 161;
        }

        // Activity level multiplier
        double activityMultiplier = switch (profile.getActivityLevel() != null ?
                profile.getActivityLevel().toLowerCase() : "moderate") {
            case "sedentary" -> 1.2;
            case "light" -> 1.375;
            case "moderate" -> 1.55;
            case "active" -> 1.725;
            case "very_active" -> 1.9;
            default -> 1.55;
        };

        double tdee = bmr * activityMultiplier;

        // Adjust for goal
        if ("weight_loss".equals(profile.getGoal())) {
            tdee -= 500; // 500 calorie deficit
        } else if ("muscle_gain".equals(profile.getGoal())) {
            tdee += 300; // 300 calorie surplus
        }

        return BigDecimal.valueOf(tdee);
    }

    /**
     * Extract text content từ Gemini API response
     */
    private String extractTextFromGeminiResponse(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            return root.path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini response", e);
        }
    }
}
```

### AIMealPlanParserService.java - Parse AI Response

````java
@Service
public class AIMealPlanParserService {

    /**
     * Parse AI response và lưu vào database
     * @param aiResponse JSON response từ Gemini
     * @param mealPlan MealPlan entity để lưu data
     * @param user User owner của meal plan
     * @return List of saved MealPlanItems
     */
    @Transactional
    public List<MealPlanItem> parseAndSaveMealPlan(
            String aiResponse,
            MealPlan mealPlan,
            User user) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(cleanJsonResponse(aiResponse));

            List<MealPlanItem> items = new ArrayList<>();
            JsonNode days = root.path("days");

            for (JsonNode day : days) {
                int dayNumber = day.path("dayNumber").asInt();
                JsonNode meals = day.path("meals");

                for (JsonNode meal : meals) {
                    // Create Recipe
                    Recipe recipe = createRecipeFromMeal(meal, user);

                    // Create MealPlanItem
                    MealPlanItem item = new MealPlanItem();
                    item.setMealPlan(mealPlan);
                    item.setRecipe(recipe);
                    item.setDayOfWeek(dayNumber);
                    item.setMealType(meal.path("mealType").asText());

                    items.add(item);
                }
            }

            return items;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI meal plan", e);
        }
    }

    /**
     * Clean JSON response (remove markdown, extra text)
     */
    private String cleanJsonResponse(String response) {
        // Remove markdown code blocks
        response = response.replaceAll("```json\\s*", "");
        response = response.replaceAll("```\\s*", "");

        // Extract JSON object
        int start = response.indexOf("{");
        int end = response.lastIndexOf("}");

        if (start != -1 && end != -1) {
            return response.substring(start, end + 1);
        }

        return response;
    }

    /**
     * Create Recipe entity từ meal JSON
     */
    private Recipe createRecipeFromMeal(JsonNode meal, User user) {
        Recipe recipe = new Recipe();
        recipe.setName(meal.path("recipeName").asText());
        recipe.setDescription(meal.path("description").asText());
        recipe.setMealType(meal.path("mealType").asText());
        recipe.setInstructions(meal.path("instructions").asText());
        recipe.setPrepTime(meal.path("prepTime").asInt());
        recipe.setCookTime(meal.path("cookTime").asInt());
        recipe.setServings(meal.path("servings").asInt());
        recipe.setCreatedBy(user);

        // Create Ingredients
        List<Ingredient> ingredients = new ArrayList<>();
        JsonNode ingredientNodes = meal.path("ingredients");

        for (JsonNode ingNode : ingredientNodes) {
            Ingredient ingredient = new Ingredient();
            ingredient.setRecipe(recipe);
            ingredient.setName(ingNode.path("name").asText());
            ingredient.setQuantity(BigDecimal.valueOf(ingNode.path("quantity").asDouble()));
            ingredient.setUnit(ingNode.path("unit").asText());
            ingredient.setCalories(BigDecimal.valueOf(ingNode.path("calories").asDouble()));
            ingredient.setProtein(BigDecimal.valueOf(ingNode.path("protein").asDouble()));
            ingredient.setCarbs(BigDecimal.valueOf(ingNode.path("carbs").asDouble()));
            ingredient.setFat(BigDecimal.valueOf(ingNode.path("fat").asDouble()));

            ingredients.add(ingredient);
        }

        recipe.setIngredients(ingredients);
        return recipe;
    }
}
````

### Usage Example

```java
// In MealPlanController
@PostMapping("/generate")
public ResponseEntity<?> generateMealPlan(@RequestParam Long userId,
                                         @Valid @RequestBody MealPlanRequest request) {
    // 1. Get user profile
    UserProfile profile = userProfileService.getProfileByUserId(userId)
        .orElseThrow(() -> new RuntimeException("Profile not found"));

    // 2. Call Gemini AI
    String aiResponse = geminiAIService.generateMealPlan(profile, request.getDays());

    // 3. Parse and save to database
    MealPlan mealPlan = new MealPlan();
    mealPlan.setUser(user);
    mealPlan.setStartDate(LocalDate.parse(request.getStartDate()));
    mealPlan.setStatus("active");

    MealPlan savedMealPlan = mealPlanService.saveMealPlan(mealPlan);

    List<MealPlanItem> items = aiMealPlanParserService.parseAndSaveMealPlan(
        aiResponse, savedMealPlan, user
    );

    return ResponseEntity.ok(savedMealPlan);
}
```

## Testing

### Test Configuration

Dự án sử dụng **H2 in-memory database** cho testing, tách biệt hoàn toàn với MySQL development/production.

- Profile: `test`
- Database: H2 (auto-configured)
- Test files: `src/test/resources/application-test.properties`

### Chạy tests

```bash
# Chạy tất cả tests
mvn test

# Chạy specific test class
mvn test -Dtest=AuthServiceTest

# Chạy với coverage report
mvn clean test jacoco:report

# Clean và test
mvn clean test
```

### Test Coverage

**16 Tests Passing ✅**

1. **MpbeApplicationTests** (1 test)

   - Context loads successfully

2. **JwtTokenProviderTest** (5 tests)

   - Generate access token
   - Generate refresh token
   - Extract username from token
   - Validate token
   - Token expiration handling

3. **AuthServiceTest** (5 tests)

   - Register new user
   - Login with valid credentials
   - Handle duplicate email
   - Handle invalid credentials
   - Refresh token flow

4. **UserRecipeFavoriteServiceTest** (5 tests)
   - Add recipe to favorites
   - Remove from favorites
   - Get user favorites
   - Check if recipe is favorited
   - Handle duplicate favorites

### Test với Swagger UI

Khi chạy ở development mode:

1. Truy cập: `http://localhost:8080/swagger-ui/index.html`
2. Test các endpoints trực tiếp từ UI
3. Xem request/response schemas

### Manual API Testing

```bash
# 1. Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@test.com","password":"password123","fullName":"Test User"}'

# 2. Login
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@test.com","password":"password123"}' \
  | jq -r '.accessToken')

# 3. Access protected endpoint
curl -X GET http://localhost:8080/api/recipes \
  -H "Authorization: Bearer $TOKEN"
```

## Dependencies chính

### Core Dependencies

```xml
<!-- Spring Boot Parent -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.0</version>
</parent>

<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Database -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- H2 for Testing -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- JWT (RS512 Algorithm) -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- AOP for Logging -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-aop</artifactId>
    </dependency>
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjweaver</artifactId>
    </dependency>

    <!-- OpenAPI/Swagger Documentation -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.3.0</version>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Deployment

### Docker Deployment

#### Dockerfile

```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy JWT keys
COPY src/main/resources/private_key.pem ./
COPY src/main/resources/public_key.pem ./

# Copy JAR file
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### docker-compose.yml

```yaml
version: "3.8"

services:
  mysql:
    image: mysql:8.0
    container_name: mpbe-mysql
    environment:
      MYSQL_DATABASE: meal_planner
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD}
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    networks:
      - mpbe-network

  backend:
    build: .
    container_name: mpbe-backend
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_URL: jdbc:mysql://mysql:3306/meal_planner?createDatabaseIfNotExist=true
      DB_USERNAME: root
      DB_PASSWORD: ${DB_PASSWORD}
      GEMINI_API_KEY: ${GEMINI_API_KEY}
      JWT_PRIVATE_KEY_PATH: file:./private_key.pem
      JWT_PUBLIC_KEY_PATH: file:./public_key.pem
      ALLOWED_ORIGINS: ${ALLOWED_ORIGINS}
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    networks:
      - mpbe-network

volumes:
  mysql-data:

networks:
  mpbe-network:
    driver: bridge
```

#### Build và Run

```bash
# Build image
docker build -t mpbe-backend:latest .

# Run với docker-compose
docker-compose up -d

# Check logs
docker-compose logs -f backend

# Stop services
docker-compose down

# Stop và remove volumes
docker-compose down -v
```

### Environment Variables cho Production

#### .env file (cho docker-compose)

```bash
# Database
DB_PASSWORD=your_secure_password

# Gemini AI
GEMINI_API_KEY=your_gemini_api_key

# CORS
ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com

# Optional
PORT=8080
```

#### System Environment Variables

```bash
# Linux/Mac
export SPRING_PROFILES_ACTIVE=prod
export DB_URL="jdbc:mysql://prod-db:3306/meal_planner"
export DB_USERNAME="prod_user"
export DB_PASSWORD="secure_password"
export JWT_PRIVATE_KEY_PATH="/path/to/private_key.pem"
export JWT_PUBLIC_KEY_PATH="/path/to/public_key.pem"
export GEMINI_API_KEY="your_production_key"
export ALLOWED_ORIGINS="https://yourdomain.com"

# Windows PowerShell
$env:SPRING_PROFILES_ACTIVE="prod"
$env:DB_URL="jdbc:mysql://prod-db:3306/meal_planner"
# ... etc
```

### Kubernetes Deployment (Optional)

#### deployment.yaml

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mpbe-backend
spec:
  replicas: 3
  selector:
    matchLabels:
      app: mpbe-backend
  template:
    metadata:
      labels:
        app: mpbe-backend
    spec:
      containers:
        - name: backend
          image: mpbe-backend:latest
          ports:
            - containerPort: 8080
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "prod"
            - name: DB_URL
              valueFrom:
                secretKeyRef:
                  name: mpbe-secrets
                  key: db-url
            - name: GEMINI_API_KEY
              valueFrom:
                secretKeyRef:
                  name: mpbe-secrets
                  key: gemini-api-key
          resources:
            requests:
              memory: "512Mi"
              cpu: "250m"
            limits:
              memory: "1Gi"
              cpu: "500m"
          livenessProbe:
            httpGet:
              path: /actuator/health
              port: 8080
            initialDelaySeconds: 60
            periodSeconds: 10
          readinessProbe:
            httpGet:
              path: /actuator/health
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: mpbe-backend-service
spec:
  selector:
    app: mpbe-backend
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8080
  type: LoadBalancer
```

### Cloud Platform Deployment

#### Heroku

```bash
# Login to Heroku
heroku login

# Create app
heroku create mpbe-backend

# Set environment variables
heroku config:set SPRING_PROFILES_ACTIVE=prod
heroku config:set DB_URL="your_db_url"
heroku config:set GEMINI_API_KEY="your_key"

# Deploy
git push heroku main

# View logs
heroku logs --tail
```

#### AWS Elastic Beanstalk

```bash
# Install EB CLI
pip install awsebcli

# Initialize EB
eb init -p java-17 mpbe-backend

# Create environment
eb create mpbe-prod-env

# Set environment variables
eb setenv SPRING_PROFILES_ACTIVE=prod DB_URL="your_db_url" GEMINI_API_KEY="your_key"

# Deploy
eb deploy

# Open application
eb open
```

### Production Checklist

- [ ] Set strong database passwords
- [ ] Secure JWT private key
- [ ] Configure proper CORS origins
- [ ] Enable HTTPS/TLS
- [ ] Set up database backups
- [ ] Configure monitoring and alerting
- [ ] Set up logging aggregation
- [ ] Enable rate limiting
- [ ] Configure firewall rules
- [ ] Set up CDN for static assets
- [ ] Implement health checks
- [ ] Configure auto-scaling
- [ ] Set up CI/CD pipeline

## Bảo mật

### Implemented Security Features ✅

1. **Authentication**

   - JWT với RS512 Algorithm (asymmetric encryption)
   - Access Token (1 hour) + Refresh Token (30 days)
   - Token expiry tracking và automatic cleanup

2. **Password Security**

   - BCrypt hashing với strength 10
   - Password validation (minimum 6 characters)
   - No plain text password storage

3. **Input Validation**

   - Bean Validation (@Valid, @NotBlank, @Email)
   - Custom validation cho business rules
   - Min/Max constraints cho numeric fields
   - Centralized exception handling

4. **Security Headers**

   - X-Frame-Options: DENY
   - X-Content-Type-Options: nosniff
   - X-XSS-Protection: 1; mode=block
   - CORS configuration

5. **Database Security**

   - JPA prevents SQL injection
   - Prepared statements
   - Lazy loading để tránh N+1 queries
   - @JsonManagedReference/@JsonBackReference cho circular references

6. **API Security**
   - Role-based access control
   - Protected endpoints với @PreAuthorize
   - Public endpoints: /api/auth/**, /swagger-ui/**, /v3/api-docs/\*\*

## Performance Optimization

### Implemented Optimizations ✅

1. **Database Indexing**

   - `idx_email` on users table
   - `idx_user_id` on user_profiles, meal_plans, shopping_lists
   - `idx_meal_type`, `idx_cuisine_type` on recipes
   - `idx_recipe_id` on ingredients, meal_plan_items
   - `idx_token`, `idx_expiry_date` on refresh_tokens
   - Composite indexes cho date ranges

2. **Query Optimization**

   - FetchType.LAZY cho relationships
   - Pagination với Spring Data (PagedResponse)
   - @Transactional cho data modifications
   - Batch operations trong shopping list generation

3. **Logging & Monitoring**

   - AspectJ AOP cho performance logging
   - Execution time tracking cho Controllers/Services
   - Audit trail cho data modifications
   - Configurable log levels per environment

4. **Connection Pooling**

   - HikariCP (default in Spring Boot)
   - Optimized pool settings

5. **DTO Pattern**
   - Separate request/response DTOs
   - Prevent over-fetching
   - Clean API contracts

## Architecture Highlights

### Design Patterns

- **Layered Architecture**: Controller → Service → Repository
- **DTO Pattern**: Request/Response objects
- **Builder Pattern**: Entity creation (Lombok)
- **Repository Pattern**: Spring Data JPA
- **Aspect-Oriented Programming**: Cross-cutting concerns (logging, auditing)
- **Strategy Pattern**: Profile-based configuration

### Key Features

1. **Separation of Concerns**

   - Clear layer boundaries
   - Single responsibility principle
   - Dependency injection

2. **Testability**

   - Unit tests với Mockito
   - Integration tests với H2
   - Separate test configuration

3. **Maintainability**

   - Consistent naming conventions
   - Comprehensive documentation
   - Global exception handling

4. **Scalability**
   - Stateless authentication (JWT)
   - Database indexing
   - Pagination support

## Environment-Specific Configuration

| Feature      | Development | Production  | Testing      |
| ------------ | ----------- | ----------- | ------------ |
| Database     | MySQL       | MySQL       | H2 In-Memory |
| DDL Auto     | update      | validate    | create-drop  |
| Show SQL     | true        | false       | false        |
| Log Level    | DEBUG       | WARN/INFO   | INFO         |
| Swagger      | Enabled     | Disabled    | Disabled     |
| CORS         | Permissive  | Restrictive | N/A          |
| Token Expiry | 1h/30d      | 30m/7d      | Same as dev  |

## Known Issues & Limitations

1. **H2 Foreign Key Warnings**: Warnings during test execution về foreign keys không tìm thấy bảng. Không ảnh hưởng tests (16/16 passing).

2. **Manual Timestamp Management**: Một số entities dùng `LocalDateTime.now()` thay vì JPA Auditing. Consider migrate to `@EnableJpaAuditing` trong tương lai.

3. **File Upload**: Chưa implement file upload cho recipe images. URLs được lưu dạng string.

4. **Rate Limiting**: Chưa implement rate limiting cho API endpoints.

5. **Caching**: Chưa implement Redis caching cho frequently accessed data.

## Future Enhancements

### High Priority

- [ ] Implement JPA Auditing (@CreatedDate, @LastModifiedDate)
- [ ] Add file upload service cho recipe images
- [ ] Implement rate limiting với Spring Bucket4j
- [ ] Add email verification cho user registration

### Medium Priority

- [ ] Redis caching cho recipes và meal plans
- [ ] Batch processing cho meal plan generation
- [ ] WebSocket support cho real-time updates
- [ ] Export shopping list to PDF/Excel

### Low Priority

- [ ] Social features (share recipes, meal plans)
- [ ] Integration với fitness tracking apps
- [ ] Mobile push notifications
- [ ] Advanced analytics dashboard

## Troubleshooting

### Common Issues

**Problem**: MySQL connection refused

```bash
# Solution: Check MySQL is running
sudo systemctl status mysql
# Start if needed
sudo systemctl start mysql
```

**Problem**: JWT key files not found

```bash
# Solution: Generate keys
openssl genpkey -algorithm RSA -out src/main/resources/private_key.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in src/main/resources/private_key.pem -out src/main/resources/public_key.pem
```

**Problem**: Tests failing with MySQL connection

```bash
# Solution: Tests use H2, ensure @ActiveProfiles("test") is set
# Check src/test/resources/application-test.properties exists
```

## Documentation

- **API Documentation**: http://localhost:8080/swagger-ui/index.html (dev only)
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs
- **Critical Fixes**: See `CRITICAL_FIXES.md`
- **Additional Fixes**: See `ADDITIONAL_FIXES.md`

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Write tests for new features
4. Ensure all tests pass
5. Submit a pull request

## License

MIT License

## Contact

- **Project Maintainer**: uvhnael
- **Organization**: org.uvhnael
- **Version**: 0.0.1-SNAPSHOT
