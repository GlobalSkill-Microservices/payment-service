package com.globalskills.payment_service.Payment.Service;

import com.globalskills.payment_service.Common.Dto.AccountDto;
import com.globalskills.payment_service.Payment.Dto.TransactionResponse;
import com.globalskills.payment_service.Payment.Entity.Transaction;
import com.globalskills.payment_service.Payment.Exception.TransactionException;
import com.globalskills.payment_service.Payment.Repository.TransactionRepo;
import com.globalskills.payment_service.Payment.Service.Client.AccountClientService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TransactionQueryService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    TransactionRepo transactionRepo;

    @Autowired
    AccountClientService accountClientService;


    public TransactionResponse getById(Long id){
        Transaction transaction = transactionRepo.findById(id).orElseThrow(()->new TransactionException("Cant found transaction", HttpStatus.NOT_FOUND));
        return mapTransactionToResponse(transaction);
    }


    private TransactionResponse mapTransactionToResponse(Transaction transaction) {
        Set<Long> accountIds = Stream.of(transaction.getFromUser(), transaction.getToUser())
                .collect(Collectors.toSet());
        List<AccountDto> accountDtos = accountClientService.fetchListAccount(accountIds);

        Map<Long, AccountDto> accountMap = accountDtos.stream()
                .collect(Collectors.toMap(AccountDto::getId, Function.identity()));

        TransactionResponse response = modelMapper.map(transaction, TransactionResponse.class);
        response.setFromUser(accountMap.get(transaction.getFromUser()));
        response.setToUser(accountMap.get(transaction.getToUser()));

        return response;
    }




}
