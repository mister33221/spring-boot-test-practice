# Spring Boot 測試全攻略：從基礎到實戰的完整指南

## 一、Spring Boot 測試的基礎概念
### 1.1 為什麼需要自動化測試
- 甚麼是測試?
    - 通常一個需求，都會有預期想要達成的結果，而我們就可以透過測試來驗證我們所實作的功能是否符合預期。
- 為什麼要自動化測試?
    - 我們預期達到的結果可能是一種結果，也可能是多種結果，甚至在我們所實作的功能可能會跑出無法預期的結果。我們會需要想辦法驗證我們的實作是否正確。如果使用人工來驗證的話，會需要大量的時間跟人力，也可能漏掉某些狀況。
    - 那我們就透過自動化的測試，將測試也寫成程式碼，透過程式碼來驗證我們的功能是否正確。這樣的好處是，我們可以隨時隨地的執行測試，並且可以在每次的程式碼修改後，透過測試來確保我們的功能是否正確。

### 1.2 Spring Boot 測試框架介紹

#### JUnit 5（Jupiter）
- Junit 5 是目前最新的 Junit 版本，在 Java 的生態圈中算是很流行的單元測試框架。
- Junit 大致由三個部分組成
    - Junit Platform：提供了執行測試的 API，也提供了執行測試的引擎 API。
    - Junit Jupiter：引入新語法與特性，例如 Lambda、Parameterized Test、Extension Model 等。
    - Junit Vintage：提供了向後兼容的 API，讓舊版的 Junit 3、4 可以在 Junit 5 上執行。
- Junit 5 常用的註解

- 使用 JUnit 5 的範例

#### Spring Boot Test Starter
- Spring Boot 提供了一個專門用來測試的 Starter，因為是 Spring Boot 的親兒子，開箱即用，也許 Spring Boot 有更好的整合性。
- 其中包含的依賴有
    - JUnit 5 : 主要的測試框架
    - Spring Test : Spring 提供的測試支援
    - AssertJ : 一個更好的斷言框架
    - Mockito : 用來模擬對象的工具
    - Hamcrest : 斷言庫，也支持自定義斷言
    - JSONassert : 用來比較 JSON 的工具
    - XmlUnit : 用來比較 XML 的工具
- 引用方式就是在 pom.xml 中加入 spring-boot-starter-test 這個依賴即可。
  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
  </dependency>
  ```
- 後面的章節，我們就來直接實作 Spring Boot 的測試。


### 1.3 測試種類與測試金字塔

#### 單元測試（Unit Test）
- 單元測試是針對程式中最小的可測試單元進行測試，通常是針對一個方法或一個類別進行測試。
- 單元測試追求
    - 小範圍
    - 快速
    - 獨立，不依賴其他模組
- 適合使用單元測試的場景
    - 驗證業務邏輯，如計算公式、條件判斷等
    - 驗證工具類、輔助方法，如日期轉換、加密解密等
    - 驗證單個模組的輸入與輸出是否符合預期
- 範例
  ```java
  class CalculatorTest {

      @Test
      void testAdd() {
          Calculator calculator = new Calculator();
          int result = calculator.add(2, 3);
          assertEquals(5, result);  // 驗證加法結果
      }
  }
  ```

#### 整合測試（Integration Test）
- 整合測試是針對多個模組之間的整合進行測試，通常是針對模組之間的交互作用進行測試。
- 整合測試追求
    - 中等範圍
    - 需要真實的環境
    - 需要真實的依賴
- 適合使用整合測試的場景
    - 驗證模組之間的交互作用
    - 測試資料庫操作是否符合預期
    - 模擬真實環境中的系統行為
- 範例
  ```java
  @SpringBootTest
  class UserServiceIntegrationTest {

      @Autowired
      private UserService userService;

      @Autowired
      private UserRepository userRepository;

      @Test
      void testCreateUser() {
          User user = new User("testUser", "testEmail@example.com");
          userService.createUser(user);

          User savedUser = userRepository.findByName("testUser");
          assertNotNull(savedUser);  // 驗證用戶是否成功保存
      }
  }
  ```

#### 端到端測試（End-to-End Test）
- 也就是 E2E 測試，是針對整個系統的功能進行測試，通常是針對用戶的操作流程進行測試，是最高層次的測試。
- E2E 測試追求
    - 全面性
    - 需要真實的環境
    - 需要真實的依賴
    - 模擬真實用戶行為
- 現在的 Web 專案通常是前後端分離的，前端使用 E2E 測試框架，如 Selenium、Cypress 等。而這篇文章主要是針對後端的測試，前端 Cypress 可以參考我之前的[文章](https://hackmd.io/@ohQEG7SsQoeXVwVP2-v06A/S170WynZ3)。
- 所以當我們的前端搭配後端的測試，整合起來就是 E2E 測試。

### 1.5 測試切片（Slice Testing）
- 測試切片是一種測試策略
- 指專注於系統或應用程式鍾某一層或某一類組件的測試，而非整個應用程式的測試
- 藉由限制測試範圍，將問題的範圍縮小，提高測試效率，確認每一個組件的正確性
- 又分為
    - 垂直切片測試（Vertical Slice Testing）：    
      測試一個功能的所有層次    
      例如：測試一個使用者登入功能，從前端輸入到後端驗證，再到資料庫比對。
    - 水平切片測試（Horizontal Slice Testing）：    
      測試一層的所有功能    
      例如：測試所有的 Service 功能，或是所有的 Repository 功能。

## 二、測試環境搭建與配置
接下來，我們就來實作一些測試案例，並在過程中，解釋常用的測試配置與技巧。

### 2.1 添加測試相關依賴
- Maven 配置
```xml
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<version>1.18.30</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-validation</artifactId>
		</dependency>
		<dependency>
			<groupId>org.junit.platform</groupId>
			<artifactId>junit-platform-suite-api</artifactId>
			<version>1.8.2</version>
			<scope>test</scope>
		</dependency>
		<!--swagger-->
		<dependency>
			<groupId>org.springdoc</groupId>
			<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
			<version>2.7.0</version>
		</dependency>
	</dependencies>
```
- 要注意 lombok 要寫版本，下面 plugin 也要寫，不然 IDE 可能會抓不到 lombok ，即便 import 了也沒用。
```xml
	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<excludes>
						<exclude>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
							<version>1.18.30</version>
						</exclude>
					</excludes>
				</configuration>
			</plugin>
		</plugins>
	</build>
```
- Lombok 抓不到還有可能是因為 IDE Annotation Processors 的問題，要自己到 Setting > Build, Execution, Deployment > Compiler > Annotation Processors 勾選 Enable annotation processing、Obtain processors from project classpath、Module output directory，表示要從專案的 classpath 中獲取 processors，並且將 processors 的輸出目錄設置為模塊的輸出目錄。

### 2.2 application.yml 配置
```yaml
spring:
  application:
    name: test-practice

  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop # 測試結束後刪除資料表
    show-sql: true
    open-in-view: false
    generate-ddl: true
    defer-datasource-initialization: true

# Swagger path: http://localhost:8080/swagger-ui/index.html
```

### 2.3 Entity
- 這個專案只會有一個 Entity，就是 User
```java
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    private String code;


    //    create a new user
    public static User createUser(CreateUserRequest userRequest) {
        return User.builder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .code("USER-" + System.currentTimeMillis())
                .build();
    }
}
```

### 2.4 RequestBody
- 用一個 RequestBody 提供給等等想要實作新增用戶的 API 使用
```java
@Data
public class CreateUserRequest {

    @NotNull(message = "Name is required")
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name should be between 3 and 50 characters")
    @Schema(description = "Name of the user", example = "John Doe")
    private String name;

    @NotNull(message = "Email is required")
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "Email of the user", example = "johndoe@example.com")
    private String email;
}
```

### 2.5 Repository
- 新增一個方法拿來驗證 Email 是否已經存在
```java
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(@NotNull @NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email);
}
```

### 2.6 Service
```java
@Service
public class UserService {


    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 獲取所有使用者
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 創建新使用者
    public synchronized  User createUser(CreateUserRequest userRequest) {
        // 檢查 email 是否已存在
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userRequest.getEmail());
        }

        User newUser = User.createUser(userRequest);
        return userRepository.save(newUser);
    }

    // 通過ID獲取使用者
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found."));
    }

}
```

### 2.7 Controller
- 我們先在 Controller 中寫三支 api
    - 由用戶 ID 取得用戶資訊
        - GET /user/{id}
    - 取得所有用戶資訊
        - GET /users
    - 新增用戶
        - POST /user
        - RequestBody
          ```json
          {
              "name": "testUser",
              "email": "test@example.com"
          }
          ```
```java
@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "Endpoints for managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    })
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Create a new user", description = "Add a new user to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    public ResponseEntity<User> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User to create", required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateUserRequest.class)))
            @RequestBody @Valid CreateUserRequest user) {
        User createdUser = userService.createUser(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdUser);
    }
}
```

### 2.8 Exception
- 先新增一個自定義的 UserNotFoundException
```java
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
```
- 再新增一個全域的 GlobalExceptionHandler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 處理 UserNotFoundException
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException ex) {
        return new ErrorResponse("User not found", HttpStatus.NOT_FOUND.toString(), ex.getMessage());
    }

    // 處理參數驗證失敗 (例如 @Valid 的驗證)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        // 提取所有錯誤訊息
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        return new ErrorResponse("Validation failed", HttpStatus.BAD_REQUEST.toString(), errorMessage);

    }

    // 處理 IllegalArgumentException 或其他自訂業務邏輯錯誤
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ErrorResponse("Bad request", HttpStatus.BAD_REQUEST.toString(), ex.getMessage());
    }

    // 處理其他未預期的異常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        return new ErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getMessage());
    }

    // 定義統一的錯誤響應結構
    static class ErrorResponse {
        private String error;
        private String httpStatus;
        private String message;
        private String timestamp;

        public ErrorResponse(String error, String httpStatus, String message) {
            this.error = error;
            this.httpStatus = httpStatus;
            this.message = message;
            this.timestamp = java.time.LocalDateTime.now().toString(); // 增加時間戳
        }

        public String getError() {
            return error;
        }

        public String getHttpStatus() {
            return httpStatus;
        }

        public String getMessage() {
            return message;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }
}
```
- 基本上到這樣，我們就有一個簡單的 Spring Boot 專案了，可以直接執行，並使用 Swagger 來測試 API。

## 三、單元測試（Unit Test）
### 3.1 JUnit 5 基礎知識
- 測試方法撰寫
- @BeforeEach 與 @AfterEach 的應用

### 3.2 測試 Controller 層
- 使用 @WebMvcTest 與 MockMvc 測試 HTTP 請求
- 範例：測試 GET/POST 方法


## 四、整合測試（Integration Test）
### 4.1 @SpringBootTest 深入解析
- 加載完整 Application Context 的原理
- 測試專用 Context 配置策略

### 4.2 REST API 整合測試
- @Sql 標註初始化 SQL 檔案
- @DirtiesContext 的應用
- 使用 MockMvc 與 TestRestTemplate
- 範例：模擬端到端請求

### 4.4 Mock 與 Real Service 比較
- 選擇適合測試場景的策略

## 五、RESTful API 測試
### 5.1 測試 Controller 層邏輯
- 驗證請求參數與回應
- 異常處理與錯誤回應測試

### 5.2 測試 REST Client
- 使用 @RestClientTest 測試 RestTemplate 或 WebClient

## 六、資料庫測試的最佳實踐
### 6.1 使用嵌入式資料庫
- H2 資料庫的設置與使用

### 6.2 測試資料的準備與管理
- Flyway 與 Liquibase 的應用

### 6.3 資料一致性與回滾策略
- 使用 @Transactional 減少測試污染

### 6.4 Testcontainers 的應用
- 動態測試容器的實戰

## 九、Spring Security 測試
### 9.1 認證與授權邏輯測試
- 使用 @WithMockUser 模擬使用者

### 9.2 測試 OAuth2 與 JWT
- 驗證保護資源的訪問權限

## 十、效能測試與自動化
### 10.1 測試覆蓋率分析
- 使用 Jacoco 生成測試覆蓋率報告

### 10.2 測試自動化配置
- 使用 GitHub Actions、Jenkins 等工具

<!-- ## 十一、測試最佳實踐
### 11.1 測試代碼風格與命名慣例
- 提高代碼可讀性的方法

### 11.2 測試覆蓋率監控
- 常見工具與監控策略

### 11.3 測試代碼的重構與維護
- 減少技術負債的實踐

## 十二、實戰範例與優化
### 12.1 完整測試專案範例
- 真實業務場景的測試案例

### 12.2 測試報告的生成與分析
- 提供可操作的改進建議

### 12.3 測試流程的持續優化
- 持續集成與部署中的測試改進 -->

## 結語
- 掌握 Spring Boot 測試的重要里程碑
- 持續學習與實踐，提升測試能力

###### tags: `Spring Boot` `測試` `JUnit 5` `Mockito` `Spring Security` `RESTful API` `資料庫測試` `效能測試` `CI/CD`
