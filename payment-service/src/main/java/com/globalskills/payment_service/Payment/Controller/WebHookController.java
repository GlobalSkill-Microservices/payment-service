package com.globalskills.payment_service.Payment.Controller;

import com.globalskills.payment_service.Common.Dto.BaseResponseAPI;
import com.globalskills.payment_service.Payment.Dto.WebhookRequest;
import com.globalskills.payment_service.Payment.Service.InvoiceCommandService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
@SecurityRequirement(name = "api")
public class WebHookController {

    @Autowired
    InvoiceCommandService invoiceCommandService;

    @PostMapping
    public ResponseEntity<?> update(@RequestBody WebhookRequest request) {
        invoiceCommandService.update(request);
        BaseResponseAPI<?> responseAPI = new BaseResponseAPI<>(true, "Invoice payment success", null, null);
        return ResponseEntity.ok(responseAPI);
    }
}
