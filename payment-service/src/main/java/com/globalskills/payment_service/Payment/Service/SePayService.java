package com.globalskills.payment_service.Payment.Service;

import com.globalskills.payment_service.Payment.Dto.SePayRequest;
import com.globalskills.payment_service.Payment.Dto.SePayResponse;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;

@Service
public class SePayService {

    private final String QR_BASE_URL = "https://qr.sepay.vn/img";
    private final String YOUR_BANK_ACCOUNT_NUMBER = "104877830765";
    private final String YOUR_BANK_CODE = "VietinBank";
    private final int TIMEOUT_MINUTES = 15;


    public SePayResponse createQrCode(SePayRequest request){
        try{
            String encodedDescription = URLEncoder.encode(request.getDescription(), StandardCharsets.UTF_8);
            String qrUrl = String.format("%s?acc=%s&bank=%s&amount=%d&des=%s",
                    QR_BASE_URL,
                    YOUR_BANK_ACCOUNT_NUMBER,
                    YOUR_BANK_CODE,
                    request.getAmount(),
                    encodedDescription);
            LocalTime createAt = LocalTime.now();
            LocalTime limit = createAt.plusMinutes(TIMEOUT_MINUTES);
            return new SePayResponse(createAt,limit,qrUrl);
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error encoding URL parameters",e);
        }
    }

}
