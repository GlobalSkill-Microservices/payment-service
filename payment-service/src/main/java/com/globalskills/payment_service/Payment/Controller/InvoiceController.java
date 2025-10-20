package com.globalskills.payment_service.Payment.Controller;

import com.globalskills.payment_service.Common.BaseResponseAPI;
import com.globalskills.payment_service.Payment.Dto.InvoiceRequest;
import com.globalskills.payment_service.Payment.Dto.InvoiceResponse;
import com.globalskills.payment_service.Payment.Dto.WebhookRequest;
import com.globalskills.payment_service.Payment.Service.InvoiceCommandService;
import com.globalskills.payment_service.Payment.Service.InvoiceQueryService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoice")
@SecurityRequirement(name = "api")
public class InvoiceController {

    @Autowired
    InvoiceCommandService invoiceCommandService;

    @Autowired
    InvoiceQueryService invoiceQueryService;

    @Value("${WEBHOOK_TOKEN}")
    String SEPAY_API_KEY;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody InvoiceRequest request,
                                    @Parameter(hidden = true)
                                    @RequestHeader(value = "X-User-ID",required = false) Long accountId){
        InvoiceResponse response = invoiceCommandService.create(request,accountId);
        BaseResponseAPI<InvoiceResponse> responseAPI = new BaseResponseAPI<>(true,"Create invoice successfully", response,null);
        return ResponseEntity.ok(responseAPI);
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> update(@RequestBody WebhookRequest request,
                                    @Parameter(hidden = true)
                                    @RequestHeader(value = "Authorization", required = false) String authHeader){
        if (authHeader == null || !authHeader.startsWith("apikey ") ||
                !authHeader.substring(7).equals(SEPAY_API_KEY)) {
            BaseResponseAPI<?> errorResponse = new BaseResponseAPI<>(false, "Unauthorized", null, null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        invoiceCommandService.update(request);
        BaseResponseAPI<?> responseAPI = new BaseResponseAPI<>(true,"Invoice payment success",null,null);
        return ResponseEntity.ok(responseAPI);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInvoiceById(@PathVariable Long id){
        InvoiceResponse response = invoiceQueryService.getInvoiceById(id);
        BaseResponseAPI<InvoiceResponse> responseAPI = new BaseResponseAPI<>(true,"Get invoice successfully",response,null);
        return ResponseEntity.ok(responseAPI);
    }
}
