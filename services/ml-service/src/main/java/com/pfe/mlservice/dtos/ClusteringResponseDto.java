package com.pfe.mlservice.dtos;

public class ClusteringResponseDto {
    
    private int clusterId;
    private String nomCluster;
    private String description;
    private String profilClient;
    private String recommandations;
    private double scoreAffinite;

    // Constructors
    public ClusteringResponseDto() {}

    public ClusteringResponseDto(int clusterId, String nomCluster, String description, String profilClient, String recommandations, double scoreAffinite) {
        this.clusterId = clusterId;
        this.nomCluster = nomCluster;
        this.description = description;
        this.profilClient = profilClient;
        this.recommandations = recommandations;
        this.scoreAffinite = scoreAffinite;
    }

    // Getters and Setters
    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public String getNomCluster() {
        return nomCluster;
    }

    public void setNomCluster(String nomCluster) {
        this.nomCluster = nomCluster;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfilClient() {
        return profilClient;
    }

    public void setProfilClient(String profilClient) {
        this.profilClient = profilClient;
    }

    public String getRecommandations() {
        return recommandations;
    }

    public void setRecommandations(String recommandations) {
        this.recommandations = recommandations;
    }

    public double getScoreAffinite() {
        return scoreAffinite;
    }

    public void setScoreAffinite(double scoreAffinite) {
        this.scoreAffinite = scoreAffinite;
    }
}
