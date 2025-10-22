package com.globalskills.payment_service.Payment.Repository;

import com.globalskills.payment_service.Payment.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction,Long> {

    @Query("SELECT t FROM Transaction t WHERE t.invoice.id IN :invoiceIds")
    List<Transaction> findByInvoiceIds(@Param("invoiceIds") List<Long> invoiceIds);
}
