package com.globalskills.payment_service.Payment.Entity;

import com.globalskills.payment_service.Payment.Enum.InvoiceStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long accountId;

    Long externalOrderId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    Long amount;

    String currency;

    @Enumerated(EnumType.STRING)
    InvoiceStatus invoiceStatus;

    Date createdAt;

    Date updatedAt;

    String transactionNumber;

    @OneToMany(mappedBy = "invoice")
    Set<Transaction> transactions = new HashSet<>();
}
