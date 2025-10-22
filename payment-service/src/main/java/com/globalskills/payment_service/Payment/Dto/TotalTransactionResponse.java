package com.globalskills.payment_service.Payment.Dto;

import com.globalskills.payment_service.Common.AccountDto;
import com.globalskills.payment_service.Payment.Enum.TransactionStatus;
import com.globalskills.payment_service.Payment.Enum.TransactionType;
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
public class TotalTransactionResponse {
    Long id;

    AccountDto fromUser;

    AccountDto toUser;

    TransactionType transactionType;

    TransactionStatus transactionStatus;

    Long amount;

    String gatewayTransactionId;

    String createdAt;

    TotalInvoiceResponse totalInvoiceResponse;
}
