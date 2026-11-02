package com.waggy.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waggy.dto.payment.PaymentResponseDTO;
import com.waggy.entity.Category;
import com.waggy.entity.Product;
import com.waggy.entity.Role;
import com.waggy.entity.User;
import com.waggy.repository.CategoryRepository;
import com.waggy.repository.OrderRepository;
import com.waggy.repository.ProductRepository;
import com.waggy.repository.UserRepository;
import com.waggy.service.JwtService;
import com.waggy.service.PaymentService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PaymentIntegrationTest {

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

    @MockitoBean
    private PaymentService paymentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM order_items");
        jdbcTemplate.update("DELETE FROM cart_items");
        jdbcTemplate.update("DELETE FROM carts");
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
        product.setPrice(new java.math.BigDecimal("29.99"));
        product.setStock(50);
        product.setImage("dog-food.jpg");
        product.setCategory(category);

        return productRepository.save(product);
    }

    @Test
    void createPayment_shouldReturnCheckoutUrl() throws Exception {

        User savedUser = createUser();
        Product product = createProduct();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        savedUser.getEmail(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

        String orderRequest = """
                {
                  "items": [
                    {
                      "productId": %d,
                      "quantity": 2
                    }
                  ]
                }
                """.formatted(product.getId());

        String orderResponse = mockMvc.perform(post("/orders")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer orderId = objectMapper
                .readTree(orderResponse)
                .get("id")
                .asInt();

        PaymentResponseDTO response =
                new PaymentResponseDTO("https://checkout.stripe.com/test-session");

        when(paymentService.createCheckoutSession(
                new com.waggy.dto.payment.PaymentRequestDTO(orderId)
        )).thenReturn(response);

        String paymentRequest = """
                {
                  "orderId": %d
                }
                """.formatted(orderId);

        mockMvc.perform(post("/payments")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paymentRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checkoutUrl")
                        .value("https://checkout.stripe.com/test-session"));
    }
}