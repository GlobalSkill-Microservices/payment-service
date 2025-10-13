package com.globalskills.payment_service.Payment.Entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long accountId;

    Double balance;

    String currency;

    @OneToMany(mappedBy = "sourceWallet")
    Set<Transaction> outgoingTransactions = new HashSet<>();

    @OneToMany(mappedBy = "destinationWallet")
    Set<Transaction> incomingTransactions = new HashSet<>();

}
