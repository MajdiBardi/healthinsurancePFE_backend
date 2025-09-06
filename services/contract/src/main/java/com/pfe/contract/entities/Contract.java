package com.pfe.contract.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "contrats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate creationDate;
    private LocalDate endDate;
    private String status;
    private String clientId;
    private String insurerId;

    @Column(name = "beneficiary_id")
    private String beneficiaryId;

    @Column(nullable = false)
    private Double montant;  // ✅ Nouveau champ ajouté

    // Champs pour signature électronique
    private String clientSignature; // Signature du client (base64 ou hash)
    private String insurerSignature; // Signature de l'assureur (base64 ou hash)
    private LocalDate clientSignedAt; // Date de signature du client
    private LocalDate insurerSignedAt; // Date de signature de l'assureur
    private Boolean isFullySigned; // Indique si le contrat est entièrement signé
}
