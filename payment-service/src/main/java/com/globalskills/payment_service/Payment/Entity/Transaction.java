package com.globalskills.payment_service.Payment.Entity;

import com.globalskills.payment_service.Payment.Enum.TransactionStatus;
import com.globalskills.payment_service.Payment.Enum.TransactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "source_wallet_id")
    Wallet sourceWallet;

    @ManyToOne
    @JoinColumn(name = "destination_wallet_id")
    Wallet destinationWallet;

    @Enumerated(EnumType.STRING)
    TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    TransactionStatus transactionStatus;

    Double amount;

    String gatewayTransactionId;

    Date createdAt;
}
