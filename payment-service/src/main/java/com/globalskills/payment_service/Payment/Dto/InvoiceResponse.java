package com.globalskills.payment_service.Payment.Dto;

import com.globalskills.payment_service.Payment.Enum.InvoiceStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceResponse {
    Long id;
    Long accountId;
    Long amount;
    String currency;
    InvoiceStatus invoiceStatus;
    Date createdAt;
    Date updatedAt;
    String transactionNumber;
    SePayResponse sePayResponse;
    ProductResponse productResponse;
}
