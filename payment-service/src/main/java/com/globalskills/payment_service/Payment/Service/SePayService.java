package com.globalskills.payment_service.Payment.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globalskills.payment_service.Payment.Dto.SePayRequest;
import com.globalskills.payment_service.Payment.Dto.SePayResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;

@Service
public class SePayService {

    @Autowired
    ModelMapper modelMapper;

    private final String BASE_URL = "https://my.sepay.vn/userapi";
    private final String BANK_ACCOUNT = "104877830765";
    private final String BANK_CODE = "vietinbank";
    private final String TOKEN = "UKN5WDAHKQIJKUXVGPZNJMYATQMTXEACRIWH1EC4VGPCCB89MO4WJY2SZIRLF1SM";
    private final int TIMEOUT_MINUTES = 15;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;


    public SePayResponse createQrCode(SePayRequest request) {
        try {
            // === 1. Tạo URL ===
            String url = String.format("%s/%s/%s/orders", BASE_URL, BANK_CODE, BANK_ACCOUNT);

            // === 2. Set header ===
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(TOKEN);

            // === 3. Body ===
            HttpEntity<SePayRequest> entity = new HttpEntity<>(request, headers);

            // === 4. Gọi API ===
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // === 5. Parse JSON ===
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.path("data");

            String qrUrl = data.path("qr_code_url").asText();

            // === 6. Tạo response của mình ===
            LocalTime createAt = LocalTime.now();
            LocalTime timeLimit = createAt.plusMinutes(TIMEOUT_MINUTES);

            SePayResponse result = new SePayResponse();
            result.setCreateAt(createAt);
            result.setTimeLimit(timeLimit);
            result.setQrUrl(qrUrl);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error creating Sepay QR: " + e.getMessage(), e);
        }
    }
}
