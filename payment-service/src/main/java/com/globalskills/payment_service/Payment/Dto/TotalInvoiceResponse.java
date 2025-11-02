package com.globalskills.payment_service.Payment.Dto;

import com.globalskills.payment_service.Common.Dto.AccountDto;
import com.globalskills.payment_service.Payment.Enum.InvoiceStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TotalInvoiceResponse {
    Long id;

    AccountDto accountDto;

    Long amount;

    String currency;

    InvoiceStatus invoiceStatus;

    String createdAt;

    String updatedAt;

    String transactionNumber;
}
