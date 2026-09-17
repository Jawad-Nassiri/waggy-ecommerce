package com.waggy.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waggy.entity.Category;
import com.waggy.entity.Product;
import com.waggy.entity.Role;
import com.waggy.entity.User;
import com.waggy.repository.CategoryRepository;
import com.waggy.repository.OrderRepository;
import com.waggy.repository.ProductRepository;
import com.waggy.repository.UserRepository;
import com.waggy.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM order_items");
        jdbcTemplate.update("DELETE FROM orders");
        jdbcTemplate.update("DELETE FROM products");
        jdbcTemplate.update("DELETE FROM categories");
        jdbcTemplate.update("DELETE FROM users");
    }

    private User createUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("user@test.com");
        user.setPassword("password");
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    private Product createProduct() {
        Category category = new Category();
        category.setName("Dog Food");
        category.setSlug("dog_food");
        category = categoryRepository.save(category);

        Product product = new Product();
        product.setName("Dog Food");
        product.setDescription("Premium food for dogs");
        product.setPrice(new BigDecimal("29.99"));
        product.setStock(50);
        product.setImage("dog-food.jpg");
        product.setCategory(category);

        return productRepository.save(product);
    }

    private String generateToken() {
        return jwtService.generateToken(
                "user@test.com",
                Role.USER
        );
    }

    private String generateAdminToken() {
        return jwtService.generateToken(
                "admin@test.com",
                Role.ADMIN
        );
    }

    private String createOrderRequest(Integer productId) {
        return """
                {
                    "items": [
                        {
                            "productId": %d,
                            "quantity": 2
                        }
                    ]
                }
                """.formatted(productId);
    }

    private User createAdmin() {
        User user = new User();
        user.setName("Admin User");
        user.setEmail("admin@test.com");
        user.setPassword("password");
        user.setRole(Role.ADMIN);

        return userRepository.save(user);
    }

    @Test
    void createOrder_shouldCreateOrder() throws Exception {

        createUser();
        Product product = createProduct();

        String token = generateToken();
        String request = createOrderRequest(product.getId());

        mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.items[0].productId")
                        .value(product.getId()))
                .andExpect(jsonPath("$.items[0].productName")
                        .value("Dog Food"))
                .andExpect(jsonPath("$.items[0].quantity")
                        .value(2))
                .andExpect(jsonPath("$.items[0].price")
                        .value(29.99))
                .andExpect(jsonPath("$.items[0].subtotal")
                        .value(59.98))
                .andExpect(jsonPath("$.totalPrice")
                        .value(59.98))
                .andExpect(jsonPath("$.status")
                        .value("PENDING"));
    }

    @Test
    void getAllOrders_shouldReturnOrders() throws Exception {

        createUser();
        createAdmin();

        Product product = createProduct();

        String userToken = generateToken();
        String request = createOrderRequest(product.getId());

        mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());

        String adminToken = generateAdminToken();

        mockMvc.perform(get("/orders")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].items[0].productName")
                        .value("Dog Food"))
                .andExpect(jsonPath("$[0].items[0].quantity")
                        .value(2))
                .andExpect(jsonPath("$[0].totalPrice")
                        .value(59.98));
    }

    @Test
    void getMyOrders_shouldReturnOrders() throws Exception {

        createUser();
        Product product = createProduct();

        String token = generateToken();
        String request = createOrderRequest(product.getId());

        mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());

        mockMvc.perform(get("/orders/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].items[0].productName")
                        .value("Dog Food"))
                .andExpect(jsonPath("$[0].items[0].quantity")
                        .value(2))
                .andExpect(jsonPath("$[0].totalPrice")
                        .value(59.98));
    }

    @Test
    void getOrderById_shouldReturnOrder() throws Exception {

        createUser();
        createAdmin();

        Product product = createProduct();

        String userToken = generateToken();
        String request = createOrderRequest(product.getId());

        String response = mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer orderId = objectMapper
                .readTree(response)
                .get("id")
                .asInt();

        String adminToken = generateAdminToken();

        mockMvc.perform(get("/orders/{id}", orderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.items[0].productName")
                        .value("Dog Food"))
                .andExpect(jsonPath("$.items[0].quantity")
                        .value(2))
                .andExpect(jsonPath("$.totalPrice")
                        .value(59.98));
    }

    @Test
    void deleteOrder_shouldDeleteOrder() throws Exception {

        createUser();
        createAdmin();

        Product product = createProduct();

        String userToken = generateToken();
        String request = createOrderRequest(product.getId());

        String response = mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer orderId = objectMapper
                .readTree(response)
                .get("id")
                .asInt();

        String adminToken = generateAdminToken();

        mockMvc.perform(delete("/orders/{id}", orderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/orders/{id}", orderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}