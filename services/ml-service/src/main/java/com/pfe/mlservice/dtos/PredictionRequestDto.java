package com.pfe.mlservice.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;

public class PredictionRequestDto {
    
    @NotNull(message = "Le capital initial est requis")
    @Positive(message = "Le capital initial doit être positif")
    private Double capitalInitial;
    
    @NotNull(message = "Le rendement annuel est requis")
    private Double rendementAnnuel;
    
    @NotNull(message = "La durée du contrat est requise")
    @Positive(message = "La durée du contrat doit être positive")
    private Integer dureeContratJours;
    
    @NotNull(message = "Le revenu annuel est requis")
    @Positive(message = "Le revenu annuel doit être positif")
    private Double revenuAnnuel;
    
    @NotNull(message = "Le score de risque est requis")
    @Min(value = 0, message = "Le score de risque doit être positif ou nul")
    private Double scoreRisque;
    
    @NotNull(message = "L'âge du client est requis")
    @Positive(message = "L'âge du client doit être positif")
    private Integer ageClient;
    
    @NotNull(message = "Le nombre de transactions est requis")
    @Min(value = 0, message = "Le nombre de transactions doit être positif ou nul")
    private Integer nbTransactions;
    
    @NotNull(message = "Le montant des versements est requis")
    @Min(value = 0, message = "Le montant des versements doit être positif ou nul")
    private Double montantVersements;
    
    @NotNull(message = "Le montant des rachats est requis")
    @Min(value = 0, message = "Le montant des rachats doit être positif ou nul")
    private Double montantRachats;
    
    @NotNull(message = "Le ratio de rachats est requis")
    @Min(value = 0, message = "Le ratio de rachats doit être positif ou nul")
    private Double ratioRachats;
    
    @NotNull(message = "Le nombre d'alertes est requis")
    @Min(value = 0, message = "Le nombre d'alertes doit être positif ou nul")
    private Integer nbAlertes;
    
    @NotNull(message = "Le nombre d'alertes élevées est requis")
    @Min(value = 0, message = "Le nombre d'alertes élevées doit être positif ou nul")
    private Integer nbAlertesEleve;
    
    private Boolean typeProfilPrudent;
    private Boolean typeProfilEquilibre;

    // Constructors
    public PredictionRequestDto() {}

    // Getters and Setters
    public Double getCapitalInitial() {
        return capitalInitial;
    }

    public void setCapitalInitial(Double capitalInitial) {
        this.capitalInitial = capitalInitial;
    }

    public Double getRendementAnnuel() {
        return rendementAnnuel;
    }

    public void setRendementAnnuel(Double rendementAnnuel) {
        this.rendementAnnuel = rendementAnnuel;
    }

    public Integer getDureeContratJours() {
        return dureeContratJours;
    }

    public void setDureeContratJours(Integer dureeContratJours) {
        this.dureeContratJours = dureeContratJours;
    }

    public Double getRevenuAnnuel() {
        return revenuAnnuel;
    }

    public void setRevenuAnnuel(Double revenuAnnuel) {
        this.revenuAnnuel = revenuAnnuel;
    }

    public Double getScoreRisque() {
        return scoreRisque;
    }

    public void setScoreRisque(Double scoreRisque) {
        this.scoreRisque = scoreRisque;
    }

    public Integer getAgeClient() {
        return ageClient;
    }

    public void setAgeClient(Integer ageClient) {
        this.ageClient = ageClient;
    }

    public Integer getNbTransactions() {
        return nbTransactions;
    }

    public void setNbTransactions(Integer nbTransactions) {
        this.nbTransactions = nbTransactions;
    }

    public Double getMontantVersements() {
        return montantVersements;
    }

    public void setMontantVersements(Double montantVersements) {
        this.montantVersements = montantVersements;
    }

    public Double getMontantRachats() {
        return montantRachats;
    }

    public void setMontantRachats(Double montantRachats) {
        this.montantRachats = montantRachats;
    }

    public Double getRatioRachats() {
        return ratioRachats;
    }

    public void setRatioRachats(Double ratioRachats) {
        this.ratioRachats = ratioRachats;
    }

    public Integer getNbAlertes() {
        return nbAlertes;
    }

    public void setNbAlertes(Integer nbAlertes) {
        this.nbAlertes = nbAlertes;
    }

    public Integer getNbAlertesEleve() {
        return nbAlertesEleve;
    }

    public void setNbAlertesEleve(Integer nbAlertesEleve) {
        this.nbAlertesEleve = nbAlertesEleve;
    }

    public Boolean getTypeProfilPrudent() {
        return typeProfilPrudent;
    }

    public void setTypeProfilPrudent(Boolean typeProfilPrudent) {
        this.typeProfilPrudent = typeProfilPrudent;
    }

    public Boolean getTypeProfilEquilibre() {
        return typeProfilEquilibre;
    }

    public void setTypeProfilEquilibre(Boolean typeProfilEquilibre) {
        this.typeProfilEquilibre = typeProfilEquilibre;
    }
}
