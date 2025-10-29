package com.globalskills.payment_service.Payment.Repository;

import com.globalskills.payment_service.Payment.Entity.Transaction;
import com.globalskills.payment_service.Payment.Enum.InvoiceStatus;
import com.globalskills.payment_service.Payment.Enum.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction,Long> {

    @Query("SELECT t FROM Transaction t WHERE t.transactionStatus = :ts AND t.invoice.invoiceStatus = :is")
    Page<Transaction> findByStatusAndInvoiceStatus(
            PageRequest pageRequest,
            @Param("ts") TransactionStatus transactionStatus,
            @Param("is") InvoiceStatus invoiceStatus
    );
}
