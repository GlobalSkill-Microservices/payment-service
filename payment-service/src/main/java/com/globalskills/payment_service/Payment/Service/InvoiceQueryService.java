package com.globalskills.payment_service.Payment.Service;

import com.globalskills.payment_service.Common.Dto.AccountDto;
import com.globalskills.payment_service.Payment.Dto.InvoiceResponse;
import com.globalskills.payment_service.Payment.Entity.Invoice;
import com.globalskills.payment_service.Payment.Exception.InvoiceException;
import com.globalskills.payment_service.Payment.Repository.InvoiceRepo;
import com.globalskills.payment_service.Payment.Service.Client.AccountClientService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class InvoiceQueryService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    InvoiceRepo invoiceRepo;

    @Autowired
    AccountClientService accountClientService;

    public Invoice findById(Long id){
        return invoiceRepo.findById(id).orElseThrow(()->new InvoiceException("Cant found invoice", HttpStatus.NOT_FOUND));
    }

    public Invoice findByTransactionNumber(String transactionNumber){
        return invoiceRepo.findByTransactionNumber(transactionNumber).orElseThrow(()-> new InvoiceException("Cant found invoice",HttpStatus.NOT_FOUND));
    }

    public InvoiceResponse getInvoiceById(Long id){
        Invoice invoice = findById(id);
        AccountDto accountDto = accountClientService.fetchAccount(invoice.getAccountId());
        InvoiceResponse response = modelMapper.map(invoice, InvoiceResponse.class);
        response.setAccountId(accountDto);
        return response;
    }
}
