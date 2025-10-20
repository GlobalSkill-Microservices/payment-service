package com.globalskills.payment_service.Payment.Service;

import com.globalskills.payment_service.Common.PageResponse;
import com.globalskills.payment_service.Payment.Dto.TransactionResponse;
import com.globalskills.payment_service.Payment.Entity.Transaction;
import com.globalskills.payment_service.Payment.Exception.TransactionException;
import com.globalskills.payment_service.Payment.Repository.TransactionRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class TransactionQueryService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    TransactionRepo transactionRepo;

    public TransactionResponse getById(Long id){
        Transaction transaction = transactionRepo.findById(id).orElseThrow(()->new TransactionException("Cant found transaction", HttpStatus.NOT_FOUND));
        return modelMapper.map(transaction, TransactionResponse.class);
    }


}
