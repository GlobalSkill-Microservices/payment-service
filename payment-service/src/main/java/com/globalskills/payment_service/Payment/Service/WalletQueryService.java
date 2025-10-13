package com.globalskills.payment_service.Payment.Service;

import com.globalskills.payment_service.Common.PageResponse;
import com.globalskills.payment_service.Payment.Dto.WalletResponse;
import com.globalskills.payment_service.Payment.Entity.Wallet;
import com.globalskills.payment_service.Payment.Exception.WalletException;
import com.globalskills.payment_service.Payment.Repository.WalletRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalletQueryService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    WalletRepo walletRepo;

    public Wallet findWalletByAccountId(Long accountId){
        return walletRepo.findWalletByAccountId(accountId).orElseThrow(()-> new WalletException("Cant found wallet", HttpStatus.NOT_FOUND));
    }

    public WalletResponse getWalletByAccountId(Long accountId){
        Wallet wallet = findWalletByAccountId(accountId);
        return modelMapper.map(wallet, WalletResponse.class);
    }

    public PageResponse<WalletResponse> getAll(
            int page,
            int size,
            String sortBy,
            String sortDir
    ){
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Wallet> walletPage = walletRepo.findAll(pageRequest);
        if(walletPage.isEmpty()){
            return null;
        }
        List<WalletResponse> responses = walletPage
                .stream()
                .map(wallet -> modelMapper.map(wallet, WalletResponse.class))
                .toList();
        return new PageResponse<>(
                responses,
                page,
                size,
                walletPage.getTotalElements(),
                walletPage.getTotalPages(),
                walletPage.isLast()
        );
    }

}

