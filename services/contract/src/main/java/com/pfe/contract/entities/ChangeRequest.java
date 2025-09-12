package com.pfe.contract.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "change_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @Column(nullable = false)
    private String requesterId; // CLIENT who requested

    private String changeType; // e.g., BENEFICIARY_UPDATE, AMOUNT_UPDATE

    @Column(length = 4000)
    private String requestedChanges; // free-text description of requested modification

    @Column(nullable = false)
    private String status; // PENDING, APPROVED, REJECTED, APPLIED

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    private String decidedBy; // ADMIN or INSURER id
    private LocalDateTime decidedAt;
}




