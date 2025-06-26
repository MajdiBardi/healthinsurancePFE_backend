package com.pfe.payment.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private BigDecimal amount;
    private String status;
    private LocalDate date;
    private String validatedBy;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;
}