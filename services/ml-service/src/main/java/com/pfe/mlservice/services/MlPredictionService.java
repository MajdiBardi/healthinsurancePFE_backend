package com.pfe.mlservice.services;

import com.pfe.mlservice.dtos.PredictionRequestDto;
import com.pfe.mlservice.dtos.PredictionResponseDto;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class MlPredictionService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PredictionResponseDto predictContractTermination(PredictionRequestDto request) {
        try {
            // Appeler le script Python pour la prédiction
            Map<String, Object> inputData = createInputMap(request);
            String jsonInput = objectMapper.writeValueAsString(inputData);
            
            // Exécuter le script Python
            ProcessBuilder processBuilder = new ProcessBuilder("py", "src/main/resources/ml_models/ml_predictor.py");
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
                
                return new PredictionResponseDto(
                    result.get("rachat_anticipe").asBoolean(),
                    result.get("probabilite_rachat").asDouble(),
                    result.get("niveau_risque").asText(),
                    result.get("recommandation").asText(),
                    result.get("message").asText()
                );
            } else {
                throw new RuntimeException("Erreur Python: " + errorOutput.toString());
            }
            
        } catch (Exception e) {
            // En cas d'erreur, utiliser la logique de fallback
            return predictWithBusinessRules(request);
        }
    }

    private Map<String, Object> createInputMap(PredictionRequestDto request) {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("capital_initial", request.getCapitalInitial());
        inputData.put("rendement_annuel", request.getRendementAnnuel());
        inputData.put("duree_contrat_jours", request.getDureeContratJours());
        inputData.put("revenu_annuel", request.getRevenuAnnuel());
        inputData.put("score_risque", request.getScoreRisque());
        inputData.put("age_client", request.getAgeClient());
        inputData.put("nb_transactions", request.getNbTransactions());
        inputData.put("montant_versements", request.getMontantVersements());
        inputData.put("montant_rachats", request.getMontantRachats());
        inputData.put("ratio_rachats", request.getRatioRachats());
        inputData.put("nb_alertes", request.getNbAlertes());
        inputData.put("nb_alertes_eleve", request.getNbAlertesEleve());
        inputData.put("type_profil_prudent", request.getTypeProfilPrudent() != null ? request.getTypeProfilPrudent() : false);
        inputData.put("type_profil_equilibre", request.getTypeProfilEquilibre() != null ? request.getTypeProfilEquilibre() : false);
        return inputData;
    }

    private PredictionResponseDto predictWithBusinessRules(PredictionRequestDto request) {
        try {
            // Logique de fallback basée sur des règles métier
            double riskScore = calculateRiskScore(request);
            boolean willTerminate = riskScore > 0.6;
            double confidence = Math.min(0.95, Math.max(0.1, riskScore));

            String niveauRisque = determineRiskLevel(riskScore);
            String recommandation = generateRecommendation(riskScore, willTerminate);

            return new PredictionResponseDto(
                willTerminate,
                confidence,
                niveauRisque,
                recommandation,
                "Prédiction basée sur des règles métier (fallback)"
            );
        } catch (Exception e) {
            // En cas d'erreur, retourner une réponse par défaut
            return new PredictionResponseDto(
                false,
                0.5,
                "Risque moyen",
                "Analyse manuelle recommandée",
                "Erreur lors de la prédiction: " + e.getMessage()
            );
        }
    }

    private double calculateRiskScore(PredictionRequestDto request) {
        // Simulation basée sur les features du modèle Logistic Regression
        double score = 0.0;
        
        // Facteur âge (plus jeune = plus de risque)
        if (request.getAgeClient() < 25) {
            score += 0.3;
        } else if (request.getAgeClient() < 35) {
            score += 0.2;
        } else if (request.getAgeClient() < 50) {
            score += 0.1;
        }
        
        // Facteur revenus (revenus faibles = plus de risque)
        if (request.getRevenuAnnuel() < 20000) {
            score += 0.4;
        } else if (request.getRevenuAnnuel() < 40000) {
            score += 0.2;
        }
        
        // Facteur capital initial (capitaux élevés = plus de risque)
        if (request.getCapitalInitial() > 50000) {
            score += 0.2;
        }
        
        // Facteur ratio de rachats (ratio élevé = plus de risque)
        if (request.getRatioRachats() > 0.1) {
            score += 0.3;
        } else if (request.getRatioRachats() > 0.05) {
            score += 0.1;
        }
        
        // Facteur nombre d'alertes
        if (request.getNbAlertes() > 3) {
            score += 0.2;
        } else if (request.getNbAlertes() > 1) {
            score += 0.1;
        }
        
        // Facteur score de risque
        if (request.getScoreRisque() > 0.7) {
            score += 0.3;
        } else if (request.getScoreRisque() > 0.4) {
            score += 0.1;
        }
        
        return Math.min(1.0, Math.max(0.0, score));
    }

    private String determineRiskLevel(double riskScore) {
        if (riskScore < 0.3) {
            return "Risque faible";
        } else if (riskScore < 0.6) {
            return "Risque moyen";
        } else if (riskScore < 0.8) {
            return "Risque élevé";
        } else {
            return "Risque très élevé";
        }
    }

    private String generateRecommendation(double riskScore, boolean willTerminate) {
        if (willTerminate) {
            if (riskScore > 0.8) {
                return "Rachat anticipé très probable - Proposer des alternatives ou révision du contrat";
            } else {
                return "Rachat anticipé probable - Surveiller le client et proposer des ajustements";
            }
        } else {
            if (riskScore < 0.3) {
                return "Client stable - Maintenir la relation et proposer des produits complémentaires";
            } else {
                return "Client à surveiller - Renforcer la communication et l'accompagnement";
            }
        }
    }
}
