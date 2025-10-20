package com.globalskills.payment_service.Payment.Service;

import com.globalskills.payment_service.Common.PageResponse;
import com.globalskills.payment_service.Payment.Dto.DailyRevenueResponse;
import com.globalskills.payment_service.Payment.Dto.ProductPerformanceResponse;
import com.globalskills.payment_service.Payment.Dto.TransactionStatusResponse;
import com.globalskills.payment_service.Payment.Entity.Invoice;
import com.globalskills.payment_service.Payment.Entity.Transaction;
import com.globalskills.payment_service.Payment.Enum.InvoiceStatus;
import com.globalskills.payment_service.Payment.Enum.TransactionStatus;
import com.globalskills.payment_service.Payment.Repository.InvoiceRepo;
import com.globalskills.payment_service.Payment.Repository.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {


    @Autowired
    InvoiceRepo invoiceRepo;

    @Autowired
    TransactionRepo transactionRepo;


    public PageResponse<DailyRevenueResponse> getDailyRevenue(int page, int size){
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Invoice> invoicePage = invoiceRepo.findAllByInvoiceStatusOrderByCreatedAtDesc(InvoiceStatus.PAID,pageRequest);
        if(invoicePage.isEmpty()){
            return null;
        }
        Map<String,Long> dailyRevenueMap = invoicePage
                .getContent()
                .stream()
                .collect(Collectors.groupingBy(
                        invoice -> formatDateToYMD(invoice.getCreatedAt()),
                        Collectors.summingLong(Invoice::getAmount)
                ));

        List<DailyRevenueResponse> responses = dailyRevenueMap
                .entrySet()
                .stream()
                .map(entry -> new DailyRevenueResponse(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(DailyRevenueResponse::getDate).reversed())
                .toList();

        return new PageResponse<>(
                responses,
                page,
                size,
                invoicePage.getTotalElements(),
                invoicePage.getTotalPages(),
                invoicePage.isLast()
        );
    }

    public PageResponse<ProductPerformanceResponse> getProductPerformance(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Invoice> invoicePage = invoiceRepo.findAllByInvoiceStatusOrderByCreatedAtDesc(InvoiceStatus.PAID, pageRequest);

        if (invoicePage.isEmpty()) {
            return null;
        }

        Map<Long, ProductPerformanceResponse> productRevenueMap = invoicePage
                .getContent()
                .stream()
                .collect(Collectors.groupingBy(
                        invoice -> invoice.getProduct().getId(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                invoices -> {
                                    Long productId = invoices.get(0).getProduct().getId();
                                    String productName = invoices.get(0).getProduct().getName();
                                    Long totalRevenue = invoices.stream()
                                            .mapToLong(Invoice::getAmount)
                                            .sum();
                                    return new ProductPerformanceResponse(productId, productName, totalRevenue);
                                }
                        )
                ));

        List<ProductPerformanceResponse> responses = productRevenueMap
                .values()
                .stream()
                .sorted(Comparator.comparing(ProductPerformanceResponse::getTotalRevenue).reversed())
                .toList();

        return new PageResponse<>(
                responses,
                page,
                size,
                invoicePage.getTotalElements(),
                invoicePage.getTotalPages(),
                invoicePage.isLast()
        );
    }


    public Map<String, Object> getTransactionStatistics() {

        List<Transaction> transactions = transactionRepo.findAll();

        if (transactions.isEmpty()) {
            Map<String, Object> emptyResponse = new HashMap<>();
            emptyResponse.put("data", List.of());
            emptyResponse.put("totalTransactions", 0);
            emptyResponse.put("successRate", 0.0);
            return emptyResponse;
        }

        Map<TransactionStatus, Long> countMap = transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getTransactionStatus,
                        Collectors.counting()
                ));

        List<TransactionStatusResponse> data = countMap.entrySet()
                .stream()
                .map(e -> new TransactionStatusResponse(e.getKey(), e.getValue()))
                .toList();

        long totalTransactions = transactions.size();
        long successCount = countMap.getOrDefault(TransactionStatus.SUCCESS, 0L);

        double successRate = totalTransactions > 0
                ? (double) successCount / totalTransactions
                : 0.0;

        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("totalTransactions", totalTransactions);
        response.put("successRate", successRate);

        return response;
    }






    private String formatDateToYMD(Date date) {
        if (date == null) return null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }


}
