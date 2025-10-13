package com.globalskills.payment_service.Payment.Repository;

import com.globalskills.payment_service.Payment.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction,Long> {
}
