package com.pfe.payment.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    private String id;

    private BigDecimal amount;

    private String status; // PENDING, PAID, FAILED

    private String paymeeTransactionId;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;
}