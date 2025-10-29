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
import com.globalskills.payment_service.Payment.Service.Client.AccountClientService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DashboardService {


    @Autowired
    InvoiceRepo invoiceRepo;

    @Autowired
    TransactionRepo transactionRepo;

    @Autowired
    AccountClientService accountClientService;

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

    @Transactional(readOnly = true)
    public PageResponse<TotalTransactionResponse> getTotalTransaction(
            int page,
            int size,
            String sortBy,
            String sortDir
    ){
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Transaction> transactionPage = transactionRepo.findAll(pageRequest);

        if (transactionPage.isEmpty()) {
            return new PageResponse<>(List.of(), page, size, 0, 0, true);
        }

        List<Transaction> transactions = transactionPage.getContent();

        Set<Long> invoiceIds = transactions
                .stream()
                .map(tx-> tx.getInvoice().getId())
                .collect(Collectors.toSet());

        Set<Long> userIds = transactions
                .stream()
                .map(tx-> tx.getInvoice().getAccountId())
                .collect(Collectors.toSet());

        Map<Long,Invoice> invoiceMap = invoiceRepo.findAllById(invoiceIds)
                .stream()
                .collect(Collectors.toMap(Invoice::getId, Function.identity()));

        Map<Long, AccountDto> userMap = fetchUserMap(userIds);

        List<TotalTransactionResponse> responses = transactions.stream()
                .map(transaction -> mapToTotalTransactionResponse(transaction, invoiceMap, userMap))
                .toList();

        // 6. Trả về PageResponse
        return new PageResponse<>(
                responses,
                page,
                size,
                transactionPage.getTotalElements(),
                transactionPage.getTotalPages(),
                transactionPage.isLast()
        );
    }

    private TotalTransactionResponse mapToTotalTransactionResponse(
            Transaction transaction,
            Map<Long, Invoice> invoiceMap,
            Map<Long, AccountDto> userMap
    ){
        Invoice invoice = invoiceMap.get(transaction.getInvoice().getId());
        TotalTransactionResponse totalTransactionResponse = modelMapper.map(transaction, TotalTransactionResponse.class);
        totalTransactionResponse.setCreatedAt(formatDateToYMD(transaction.getCreatedAt()));
        if (invoice != null) {
            TotalInvoiceResponse invoiceResponse = modelMapper.map(invoice, TotalInvoiceResponse.class);

            invoiceResponse.setAccountDto(userMap.get(invoice.getAccountId()));

            invoiceResponse.setCreatedAt(formatDateToYMD(invoice.getCreatedAt()));
            invoiceResponse.setUpdatedAt(formatDateToYMD(invoice.getUpdatedAt()));

            totalTransactionResponse.setTotalInvoiceResponse(invoiceResponse);
        }

        return totalTransactionResponse;
    }

    private Map<Long, AccountDto> fetchUserMap(Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            return accountClientService.fetchListAccount(userIds)
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(
                            AccountDto::getId,
                            Function.identity(),
                            (existing, replacement) -> existing
                    ));
        } catch (Exception e) {
            log.error("Failed to fetch accounts for ids: {}", userIds, e);
            return Collections.emptyMap();
        }
    }

    private String formatDateToYMD(Date date) {
        if (date == null) return null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }


}
