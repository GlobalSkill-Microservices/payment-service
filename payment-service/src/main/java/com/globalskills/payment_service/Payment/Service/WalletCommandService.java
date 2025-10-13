package com.globalskills.payment_service.Payment.Service;

import com.globalskills.payment_service.Payment.Dto.WalletRequest;
import com.globalskills.payment_service.Payment.Dto.WalletResponse;
import com.globalskills.payment_service.Payment.Entity.Wallet;
import com.globalskills.payment_service.Payment.Repository.WalletRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletCommandService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    WalletRepo walletRepo;

    @Autowired
    WalletQueryService walletQueryService;


    public WalletResponse create(Long accountId){
        Wallet wallet = new Wallet();
        wallet.setAccountId(accountId);
        wallet.setBalance(0.0);
        wallet.setCurrency("vnd");
        walletRepo.save(wallet);
        return modelMapper.map(wallet, WalletResponse.class);
    }

    public WalletResponse update(WalletRequest request, Long accountId){
        Wallet oldWallet = walletQueryService.findWalletByAccountId(accountId);
        modelMapper.map(request,oldWallet);
        walletRepo.save(oldWallet);
        return modelMapper.map(oldWallet, WalletResponse.class);
    }


}
