package com.globalskills.payment_service.Payment.Exception;

import com.globalskills.payment_service.Common.BaseException;
import org.springframework.http.HttpStatus;

public class TransactionException extends BaseException {
    public TransactionException(String message, HttpStatus status) {
        super(message,status);
    }
}
