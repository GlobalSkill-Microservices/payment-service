package com.globalskills.payment_service.Payment.Controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invoice")
@SecurityRequirement(name = "api")
public class TransactionController {
}
