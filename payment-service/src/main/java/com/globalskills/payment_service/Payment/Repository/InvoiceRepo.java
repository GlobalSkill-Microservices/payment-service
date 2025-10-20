package com.globalskills.payment_service.Payment.Repository;

import com.globalskills.payment_service.Payment.Entity.Invoice;
import com.globalskills.payment_service.Payment.Enum.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepo extends JpaRepository<Invoice,Long> {
    Optional<Invoice> findByAccountIdAndTransactionNumber(Long accountId, String transactionNumber);


    Page<Invoice> findAllByInvoiceStatusOrderByCreatedAtDesc(InvoiceStatus status, Pageable pageable);


}
