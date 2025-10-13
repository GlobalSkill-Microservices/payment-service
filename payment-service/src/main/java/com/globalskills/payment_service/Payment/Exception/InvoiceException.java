package com.globalskills.payment_service.Payment.Exception;

import com.globalskills.payment_service.Common.BaseException;
import org.springframework.http.HttpStatus;

public class InvoiceException extends BaseException {
    public InvoiceException(String message, HttpStatus status) {
        super(message,status);
    }
}
