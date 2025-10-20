package com.globalskills.payment_service.Payment.Dto;

import com.globalskills.payment_service.Payment.Enum.TransactionStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionStatusResponse {
    TransactionStatus transactionStatus;
    Long count;
}
