package com.globalskills.payment_service.Payment.Service;

import com.globalskills.payment_service.Payment.Dto.InvoiceRequest;
import com.globalskills.payment_service.Payment.Dto.InvoiceResponse;
import com.globalskills.payment_service.Payment.Entity.Invoice;
import com.globalskills.payment_service.Payment.Entity.Product;
import com.globalskills.payment_service.Payment.Enum.InvoiceStatus;
import com.globalskills.payment_service.Payment.Exception.InvoiceException;
import com.globalskills.payment_service.Payment.Repository.InvoiceRepo;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@Service
public class InvoiceCommandService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    InvoiceRepo invoiceRepo;

    @Autowired
    ProductQueryService productQueryService;

    @Autowired
    InvoiceQueryService invoiceQueryService;

    public InvoiceResponse create (HttpServletRequest servletRequest,InvoiceRequest request,Long accountId) throws Exception{
        Product product = productQueryService.findProductById(request.getProductId());
        Invoice newInvoice = new Invoice();
        newInvoice.setAccountId(accountId);
        newInvoice.setProduct(product);
        newInvoice.setAmount(product.getPrice());
        newInvoice.setCurrency(product.getCurrency());
        newInvoice.setCreatedAt(new Date());
        newInvoice.setInvoiceStatus(InvoiceStatus.PENDING);
        invoiceRepo.save(newInvoice);
        InvoiceResponse response = modelMapper.map(newInvoice,InvoiceResponse.class);
        String urlPayment = urlPayment(servletRequest,newInvoice.getId());
        response.setPaymentUrl(urlPayment);
        return response;
    }

    private String urlPayment(HttpServletRequest request,Long id) throws Exception{
        Invoice invoice = invoiceQueryService.findById(id);
        if(invoice.getInvoiceStatus() == InvoiceStatus.PENDING){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            LocalDateTime createDate = LocalDateTime.now();
            String formattedCreateDate = createDate.format(formatter);

            String orderID = String.valueOf(invoice.getId());
            double totalPrice = invoice.getAmount() * 100;
            String amount = String.valueOf((int) totalPrice);

            String tmnCode = "F66L391K";
            String secretKey = "LJ3ASJLRI88WWBDBH8CYQBDJTCW1PU0R";
            String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
            String returnUrl = "http://localhost:5173/payment-success?orderID=" + invoice.getId();
            String currCode = "VND";

            Map<String, String> vnpParams = new TreeMap<>();
            vnpParams.put("vnp_Version", "2.1.0");
            vnpParams.put("vnp_Command", "pay");
            vnpParams.put("vnp_TmnCode", tmnCode);
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_CurrCode", currCode);
            vnpParams.put("vnp_TxnRef", orderID);
            vnpParams.put("vnp_OrderInfo", "Thanh toan cho ma GD: " + invoice.getId());
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_Amount", amount);

            vnpParams.put("vnp_ReturnUrl", returnUrl);
            vnpParams.put("vnp_CreateDate", formattedCreateDate);
            vnpParams.put("vnp_IpAddr", getClientIpAddr(request));

            StringBuilder signDataBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
                signDataBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
                signDataBuilder.append("=");
                signDataBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
                signDataBuilder.append("&");
            }
            signDataBuilder.deleteCharAt(signDataBuilder.length() - 1); // Remove last '&'

            String signData = signDataBuilder.toString();
            String signed = generateHMAC(secretKey, signData);

            vnpParams.put("vnp_SecureHash", signed);

            StringBuilder urlBuilder = new StringBuilder(vnpUrl);
            urlBuilder.append("?");
            for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
                urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
                urlBuilder.append("=");
                urlBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
                urlBuilder.append("&");
            }
            urlBuilder.deleteCharAt(urlBuilder.length() - 1); // Remove last '&'

            return urlBuilder.toString();
        }
        throw new InvoiceException("Can not payment yet", HttpStatus.BAD_REQUEST);
    }

    private String generateHMAC(String secretKey, String signData) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmacSha512.init(keySpec);
        byte[] hmacBytes = hmacSha512.doFinal(signData.getBytes(StandardCharsets.UTF_8));

        StringBuilder result = new StringBuilder();
        for (byte b : hmacBytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    private String getClientIpAddr(HttpServletRequest request){
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

}
