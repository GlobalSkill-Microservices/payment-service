package com.globalskills.payment_service.Payment.Service;

import com.globalskills.payment_service.Common.AccountDto;
import com.globalskills.payment_service.Common.Feign.AccountClient;
import com.globalskills.payment_service.Common.PageResponse;
import com.globalskills.payment_service.Payment.Dto.*;
import com.globalskills.payment_service.Payment.Entity.Invoice;
import com.globalskills.payment_service.Payment.Entity.Transaction;
import com.globalskills.payment_service.Payment.Enum.InvoiceStatus;
import com.globalskills.payment_service.Payment.Enum.TransactionStatus;
import com.globalskills.payment_service.Payment.Repository.InvoiceRepo;
import com.globalskills.payment_service.Payment.Repository.TransactionRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @Autowired
    AccountClient accountClient;

    @Autowired
    ModelMapper modelMapper;


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

    public PageResponse<TotalInvoiceResponse> getTotalInvoice(
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Invoice> invoicePage = invoiceRepo.findAll(pageRequest);

        if (invoicePage.isEmpty()) {
            return null;
        }

        Set<Long> userIds = new HashSet<>();
        for (Invoice invoice : invoicePage.getContent()) {
            userIds.add(invoice.getAccountId());

            Set<Transaction> safeTransactions = new HashSet<>(invoice.getTransactions());

            for (Transaction transaction : safeTransactions) {
                userIds.add(transaction.getFromUser());
                userIds.add(transaction.getToUser());
            }
        }

        Map<Long, AccountDto> userMap = Map.of();
        if (!userIds.isEmpty()) {
            userMap = accountClient.getAccountByIds(userIds)
                    .stream()
                    .collect(Collectors.toMap(AccountDto::getId, dto -> dto));
        }

        final Map<Long, AccountDto> finalUserMap = userMap;
        List<TotalInvoiceResponse> invoiceResponses = invoicePage.getContent().stream()
                .map(invoice -> {
                    TotalInvoiceResponse response = modelMapper.map(invoice, TotalInvoiceResponse.class);

                    response.setAccountDto(finalUserMap.get(invoice.getAccountId()));
                    response.setCreatedAt(formatDateToYMD(invoice.getCreatedAt()));
                    response.setUpdatedAt(formatDateToYMD(invoice.getUpdatedAt()));

                    Set<Transaction> safeTransactionsForStream = new HashSet<>(invoice.getTransactions());

                    Set<TotalTransactionResponse> transactionResponses = safeTransactionsForStream.stream()
                            .map(transaction -> {
                                TotalTransactionResponse txResponse = modelMapper.map(transaction, TotalTransactionResponse.class);
                                txResponse.setFromUser(finalUserMap.get(transaction.getFromUser()));
                                txResponse.setToUser(finalUserMap.get(transaction.getToUser()));
                                txResponse.setCreatedAt(formatDateToYMD(transaction.getCreatedAt()));

                                return txResponse;
                            })
                            .collect(Collectors.toSet());

                    response.setTransactionResponses(transactionResponses);

                    return response;
                })
                .collect(Collectors.toList());

        return new PageResponse<>(
                invoiceResponses,
                page,
                size,
                invoicePage.getTotalElements(),
                invoicePage.getTotalPages(),
                invoicePage.isLast()
        );
    }

    private String formatDateToYMD(Date date) {
        if (date == null) return null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }


}
