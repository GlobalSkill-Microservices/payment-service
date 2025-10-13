package com.globalskills.payment_service.Payment.Controller;

import com.globalskills.payment_service.Common.BaseResponseAPI;
import com.globalskills.payment_service.Payment.Dto.InvoiceRequest;
import com.globalskills.payment_service.Payment.Dto.InvoiceResponse;
import com.globalskills.payment_service.Payment.Entity.Invoice;
import com.globalskills.payment_service.Payment.Service.InvoiceCommandService;
import com.globalskills.payment_service.Payment.Service.InvoiceQueryService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoice")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class InvoiceController {

    @Autowired
    InvoiceCommandService invoiceCommandService;

    @Autowired
    InvoiceQueryService invoiceQueryService;

    @PostMapping
    public ResponseEntity<?> create(HttpServletRequest servletRequest,
                                    @RequestBody InvoiceRequest request,
                                    @Parameter(hidden = true)
                                    @RequestHeader(value = "X-User-ID",required = false) Long accountId)throws Exception{
        InvoiceResponse response = invoiceCommandService.create(servletRequest, request,accountId);
        BaseResponseAPI<InvoiceResponse> responseAPI = new BaseResponseAPI<>(true,"Create invoice successfully", response,null);
        return ResponseEntity.ok(responseAPI);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInvoiceById(@PathVariable Long id){
        InvoiceResponse response = invoiceQueryService.getInvoiceById(id);
        BaseResponseAPI<InvoiceResponse> responseAPI = new BaseResponseAPI<>(true,"Get invoice successfully",response,null);
        return ResponseEntity.ok(responseAPI);
    }
}
