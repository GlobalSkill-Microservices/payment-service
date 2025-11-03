package com.globalskills.payment_service.Payment.Service.Client;

import com.globalskills.payment_service.Common.Dto.AccountDto;
import com.globalskills.payment_service.Common.Feign.AccountClient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AccountClientService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AccountClient accountClient;

    public AccountDto fetchAccount(Long id){
        return accountClient.getAccountById(id);
    }

    public List<AccountDto> fetchListAccount(Set<Long> ids){
        return accountClient.getAccountByIds(ids);
    }

    public void updateApplicationStatus(Long id){
        accountClient.updateApplicationStatus(id);
    }

}
