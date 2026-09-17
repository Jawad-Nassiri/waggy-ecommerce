package com.waggy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waggy.dto.payment.PaymentRequestDTO;
import com.waggy.dto.payment.PaymentResponseDTO;
import com.waggy.service.JwtService;
import com.waggy.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private PaymentService paymentService;

    private PaymentRequestDTO createRequestDTO() {
        return new PaymentRequestDTO(1);
    }

    private PaymentResponseDTO createResponseDTO() {
        return new PaymentResponseDTO(
                "https://checkout.stripe.com/test-session"
        );
    }

    @Test
    void createPayment_shouldReturnCheckoutUrl() throws Exception {
        PaymentRequestDTO request = createRequestDTO();
        PaymentResponseDTO response = createResponseDTO();

        when(paymentService.createCheckoutSession(request))
                .thenReturn(response);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checkoutUrl")
                        .value("https://checkout.stripe.com/test-session"));

        verify(paymentService).createCheckoutSession(request);
    }

    @Test
    void handleWebhook_shouldReturnOk() throws Exception {
        String payload = "{\"type\":\"checkout.session.completed\"}";
        String signature = "stripe-signature";

        mockMvc.perform(post("/payments/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Stripe-Signature", signature)
                        .content(payload))
                .andExpect(status().isOk());

        verify(paymentService).handleWebhook(payload, signature);
    }
}