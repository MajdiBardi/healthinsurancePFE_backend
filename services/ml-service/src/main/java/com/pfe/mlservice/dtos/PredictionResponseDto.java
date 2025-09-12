package com.pfe.mlservice.dtos;

public class PredictionResponseDto {
    
    private boolean rachatAnticipe;
    private double probabiliteRachat;
    private String niveauRisque;
    private String recommandation;
    private String message;

    // Constructors
    public PredictionResponseDto() {}

    public PredictionResponseDto(boolean rachatAnticipe, double probabiliteRachat, String niveauRisque, String recommandation, String message) {
        this.rachatAnticipe = rachatAnticipe;
        this.probabiliteRachat = probabiliteRachat;
        this.niveauRisque = niveauRisque;
        this.recommandation = recommandation;
        this.message = message;
    }

    // Getters and Setters
    public boolean isRachatAnticipe() {
        return rachatAnticipe;
    }

    public void setRachatAnticipe(boolean rachatAnticipe) {
        this.rachatAnticipe = rachatAnticipe;
    }

    public double getProbabiliteRachat() {
        return probabiliteRachat;
    }

    public void setProbabiliteRachat(double probabiliteRachat) {
        this.probabiliteRachat = probabiliteRachat;
    }

    public String getNiveauRisque() {
        return niveauRisque;
    }

    public void setNiveauRisque(String niveauRisque) {
        this.niveauRisque = niveauRisque;
    }

    public String getRecommandation() {
        return recommandation;
    }

    public void setRecommandation(String recommandation) {
        this.recommandation = recommandation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
