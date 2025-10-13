package com.globalskills.payment_service.Payment.Repository;

import com.globalskills.payment_service.Payment.Entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepo extends JpaRepository<Wallet,Long> {
    Optional<Wallet> findWalletByAccountId(Long accountId);
}
