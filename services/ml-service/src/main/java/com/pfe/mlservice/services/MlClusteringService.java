package com.pfe.mlservice.services;

import com.pfe.mlservice.dtos.ClusteringRequestDto;
import com.pfe.mlservice.dtos.ClusteringResponseDto;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class MlClusteringService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ClusteringResponseDto predictCluster(ClusteringRequestDto request) {
        try {
            // Appeler le script Python pour le clustering
            Map<String, Object> inputData = createInputMap(request);
            String jsonInput = objectMapper.writeValueAsString(inputData);
            
            // Exécuter le script Python
            ProcessBuilder processBuilder = new ProcessBuilder("py", "src/main/resources/ml_models/ml_clustering.py");
            processBuilder.directory(new File(System.getProperty("user.dir")));
            
            Process process = processBuilder.start();
            
            // Envoyer les données au script
            try (OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write(jsonInput);
                writer.flush();
            }
            
            // Lire la réponse
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
            }
            
            // Lire les erreurs
            StringBuilder errorOutput = new StringBuilder();
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorOutput.append(line);
                }
            }
            
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                // Parser la réponse JSON
                JsonNode result = objectMapper.readTree(output.toString());
                
                return new ClusteringResponseDto(
                    result.get("cluster_id").asInt(),
                    result.get("nom_cluster").asText(),
                    result.get("description").asText(),
                    result.get("profil_client").asText(),
                    result.get("recommandations").asText(),
                    result.get("score_affinite").asDouble()
                );
            } else {
                throw new RuntimeException("Erreur Python: " + errorOutput.toString());
            }
            
        } catch (Exception e) {
            // En cas d'erreur, utiliser la logique de fallback
            return clusterWithBusinessRules(request);
        }
    }

    private Map<String, Object> createInputMap(ClusteringRequestDto request) {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("capital_initial", request.getCapitalInitial());
        inputData.put("rendement_annuel", request.getRendementAnnuel());
        inputData.put("revenu_annuel", request.getRevenuAnnuel());
        inputData.put("score_risque", request.getScoreRisque());
        inputData.put("montant_versements", request.getMontantVersements());
        inputData.put("montant_rachats", request.getMontantRachats());
        inputData.put("ratio_rachats", request.getRatioRachats());
        inputData.put("age_client", request.getAgeClient());
        inputData.put("nb_transactions", request.getNbTransactions());
        inputData.put("nb_alertes", request.getNbAlertes());
        inputData.put("nb_alertes_eleve", request.getNbAlertesEleve());
        inputData.put("type_profil_prudent", request.getTypeProfilPrudent() != null ? request.getTypeProfilPrudent() : false);
        inputData.put("type_profil_equilibre", request.getTypeProfilEquilibre() != null ? request.getTypeProfilEquilibre() : false);
        return inputData;
    }

    private ClusteringResponseDto clusterWithBusinessRules(ClusteringRequestDto request) {
        try {
            // Logique de fallback basée sur des règles métier
            int cluster = determineCluster(request);
            String clusterDescription = getClusterDescription(cluster);
            double confidence = calculateClusterConfidence(request, cluster);
            String profilClient = getClusterCharacteristics(cluster);
            String recommandations = getClusterRecommendations(cluster);

            return new ClusteringResponseDto(
                cluster,
                clusterDescription,
                "Segmentation basée sur des règles métier (fallback)",
                profilClient,
                recommandations,
                confidence
            );
        } catch (Exception e) {
            // En cas d'erreur, retourner une réponse par défaut
            return new ClusteringResponseDto(
                0,
                "Cluster par défaut",
                "Erreur lors de la segmentation: " + e.getMessage(),
                "Profil non défini",
                "Analyse manuelle recommandée",
                0.5
            );
        }
    }

    private int determineCluster(ClusteringRequestDto request) {
        // Simulation basée sur les 4 clusters K-Means du notebook
        double age = request.getAgeClient();
        double income = request.getRevenuAnnuel();
        double capitalInitial = request.getCapitalInitial();
        double scoreRisque = request.getScoreRisque();
        double ratioRachats = request.getRatioRachats();
        int nbAlertes = request.getNbAlertes();
        boolean isPrudent = request.getTypeProfilPrudent() != null && request.getTypeProfilPrudent();
        boolean isEquilibre = request.getTypeProfilEquilibre() != null && request.getTypeProfilEquilibre();

        // Cluster 0: Clients prudents à revenus moyens (727 clients)
        if (isPrudent && income >= 30000 && income <= 60000 && scoreRisque < 0.5) {
            return 0;
        }
        
        // Cluster 1: Clients équilibrés jeunes (792 clients)
        if (isEquilibre && age < 40 && income >= 40000 && ratioRachats < 0.1) {
            return 1;
        }
        
        // Cluster 2: Clients à haut capital (797 clients)
        if (capitalInitial > 100000 && income > 50000 && nbAlertes <= 2) {
            return 2;
        }
        
        // Cluster 3: Clients à risque (14 clients)
        if (scoreRisque > 0.7 || ratioRachats > 0.2 || nbAlertes > 5) {
            return 3;
        }
        
        // Cluster par défaut (basé sur les caractéristiques principales)
        if (age < 35) return 1; // Jeunes
        if (capitalInitial > 50000) return 2; // Haut capital
        return 0; // Prudent par défaut
    }

    private String getClusterDescription(int cluster) {
        switch (cluster) {
            case 0: return "Clients prudents à revenus moyens";
            case 1: return "Clients équilibrés jeunes";
            case 2: return "Clients à haut capital";
            case 3: return "Clients à risque";
            default: return "Cluster non défini";
        }
    }

    private double calculateClusterConfidence(ClusteringRequestDto request, int cluster) {
        // Calcul de confiance basé sur la cohérence des caractéristiques
        double confidence = 0.5;
        
        switch (cluster) {
            case 0: // Clients prudents à revenus moyens
                if (request.getTypeProfilPrudent() != null && request.getTypeProfilPrudent() && 
                    request.getRevenuAnnuel() >= 30000 && request.getRevenuAnnuel() <= 60000) confidence = 0.9;
                else if (request.getScoreRisque() < 0.5) confidence = 0.7;
                break;
            case 1: // Clients équilibrés jeunes
                if (request.getTypeProfilEquilibre() != null && request.getTypeProfilEquilibre() && 
                    request.getAgeClient() < 35 && request.getRevenuAnnuel() > 40000) confidence = 0.9;
                else if (request.getAgeClient() < 40) confidence = 0.7;
                break;
            case 2: // Clients à haut capital
                if (request.getCapitalInitial() > 100000 && request.getRevenuAnnuel() > 50000) confidence = 0.9;
                else if (request.getCapitalInitial() > 50000) confidence = 0.7;
                break;
            case 3: // Clients à risque
                if (request.getScoreRisque() > 0.7 || request.getRatioRachats() > 0.2) confidence = 0.9;
                else if (request.getNbAlertes() > 3) confidence = 0.7;
                break;
        }
        
        return Math.min(0.95, Math.max(0.1, confidence));
    }

    private String getClusterCharacteristics(int cluster) {
        switch (cluster) {
            case 0:
                return "Profil: Prudent | Revenus: 30k-60k | Score risque: <0.5 | Stabilité: Élevée";
            case 1:
                return "Profil: Équilibré | Âge: <40 ans | Revenus: >40k | Ratio rachats: <0.1";
            case 2:
                return "Profil: Haut capital | Capital: >100k | Revenus: >50k | Alertes: ≤2";
            case 3:
                return "Profil: Risque élevé | Score risque: >0.7 | Ratio rachats: >0.2 | Alertes: >5";
            default:
                return "Profil non défini - Analyse approfondie nécessaire";
        }
    }

    private String getClusterRecommendations(int cluster) {
        switch (cluster) {
            case 0:
                return "Produits sécurisés, Épargne retraite, Assurance vie traditionnelle, Suivi régulier";
            case 1:
                return "Produits équilibrés, Assurance vie mixte, Investissements modérés, Croissance";
            case 2:
                return "Produits premium, Gestion de patrimoine, Investissements sophistiqués, Services VIP";
            case 3:
                return "Suivi renforcé, Produits sécurisés, Accompagnement personnalisé, Réduction risques";
            default:
                return "Analyse complète nécessaire avant recommandations";
        }
    }
}
