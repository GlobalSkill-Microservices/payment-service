package com.globalskills.payment_service.Payment.Exception;

import com.globalskills.payment_service.Common.BaseException;
import org.springframework.http.HttpStatus;

public class WalletException extends BaseException {
    public WalletException(String message, HttpStatus status) {
        super(message,status);
    }
}
