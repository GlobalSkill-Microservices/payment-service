package com.globalskills.payment_service.Payment.Service;

import com.globalskills.payment_service.Payment.Dto.TransactionRequest;
import com.globalskills.payment_service.Payment.Dto.TransactionResponse;
import com.globalskills.payment_service.Payment.Entity.Invoice;
import com.globalskills.payment_service.Payment.Entity.Transaction;
import com.globalskills.payment_service.Payment.Enum.TransactionStatus;
import com.globalskills.payment_service.Payment.Enum.TransactionType;
import com.globalskills.payment_service.Payment.Repository.TransactionRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TransactionCommandService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    TransactionQueryService transactionQueryService;

    @Autowired
    TransactionRepo transactionRepo;

    @Autowired
    InvoiceQueryService invoiceQueryService;

    public TransactionResponse create(TransactionRequest request){
        Invoice invoice = invoiceQueryService.findById(request.getInvoiceId());
        Transaction transaction = new Transaction();
        transaction.setFromUser(request.getFromUser());
        transaction.setToUser(request.getToUser());
        transaction.setTransactionType(TransactionType.PAYMENT_GATEWAY);
        transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        transaction.setAmount(request.getAmount());
        transaction.setGatewayTransactionId(request.getGatewayTransactionId());
        transaction.setCreatedAt(new Date());
        transaction.setInvoice(invoice);
        transactionRepo.save(transaction);
        return modelMapper.map(transaction, TransactionResponse.class);
    }




}
