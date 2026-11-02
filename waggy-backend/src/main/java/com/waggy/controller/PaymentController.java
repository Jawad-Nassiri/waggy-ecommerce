package com.waggy.controller;

import com.waggy.dto.payment.PaymentRequestDTO;
import com.waggy.dto.payment.PaymentResponseDTO;
import com.waggy.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @Operation(
            summary = "Create payment",
            description = "Creates a Stripe Checkout session for the order."
    )
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public PaymentResponseDTO createPayment(@Valid @RequestBody PaymentRequestDTO dto) {
        return paymentService.createCheckoutSession(dto);
    }


    @Operation(
            summary = "Handle Stripe webhook",
            description = "Receives Stripe payment events and updates the order payment status."
    )
    @PostMapping("/webhook")
    public void handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        paymentService.handleWebhook(payload, sigHeader);
    }
}
