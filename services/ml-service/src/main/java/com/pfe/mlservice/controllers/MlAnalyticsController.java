package com.pfe.mlservice.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ml/analytics")
@CrossOrigin(origins = "*")
public class MlAnalyticsController {

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Métriques de prédiction
        Map<String, Object> predictionMetrics = new HashMap<>();
        predictionMetrics.put("accuracy", 0.72);
        predictionMetrics.put("f1Score", 0.36);
        predictionMetrics.put("rocAuc", 0.80);
        predictionMetrics.put("totalPredictions", 1250);
        predictionMetrics.put("predictionsToday", 45);
        
        // Métriques de clustering
        Map<String, Object> clusteringMetrics = new HashMap<>();
        clusteringMetrics.put("totalClusters", 4);
        clusteringMetrics.put("silhouetteScore", 0.151);
        clusteringMetrics.put("totalClients", 2330);
        
        // Répartition par cluster
        Map<String, Integer> clusterDistribution = new HashMap<>();
        clusterDistribution.put("Prudents", 727);
        clusterDistribution.put("Équilibrés", 792);
        clusterDistribution.put("Agressifs", 797);
        clusterDistribution.put("VIP", 14);
        
        // Répartition des prédictions
        Map<String, Integer> predictionDistribution = new HashMap<>();
        predictionDistribution.put("Rachat Anticipé", 325);
        predictionDistribution.put("Non Rachat", 2675);
        
        dashboard.put("predictionMetrics", predictionMetrics);
        dashboard.put("clusteringMetrics", clusteringMetrics);
        dashboard.put("clusterDistribution", clusterDistribution);
        dashboard.put("predictionDistribution", predictionDistribution);
        dashboard.put("lastUpdate", "2024-12-31T12:00:00Z");
        
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/model-performance")
    public ResponseEntity<Map<String, Object>> getModelPerformance() {
        Map<String, Object> performance = new HashMap<>();
        
        // Performance des modèles
        Map<String, Object> models = new HashMap<>();
        
        Map<String, Object> logisticRegression = new HashMap<>();
        logisticRegression.put("name", "Régression Logistique");
        logisticRegression.put("accuracy", 0.72);
        logisticRegression.put("f1Score", 0.36);
        logisticRegression.put("rocAuc", 0.80);
        logisticRegression.put("trainingTime", 0.36);
        logisticRegression.put("status", "ACTIF");
        
        Map<String, Object> randomForest = new HashMap<>();
        randomForest.put("name", "Random Forest");
        randomForest.put("accuracy", 0.89);
        randomForest.put("f1Score", 0.03);
        randomForest.put("rocAuc", 0.77);
        randomForest.put("trainingTime", 0.52);
        randomForest.put("status", "BACKUP");
        
        Map<String, Object> xgboost = new HashMap<>();
        xgboost.put("name", "XGBoost");
        xgboost.put("accuracy", 0.89);
        xgboost.put("f1Score", 0.13);
        xgboost.put("rocAuc", 0.77);
        xgboost.put("trainingTime", 0.13);
        xgboost.put("status", "BACKUP");
        
        models.put("logisticRegression", logisticRegression);
        models.put("randomForest", randomForest);
        models.put("xgboost", xgboost);
        
        performance.put("models", models);
        performance.put("bestModel", "logisticRegression");
        performance.put("lastTraining", "2024-12-31T10:00:00Z");
        
        return ResponseEntity.ok(performance);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("ML Analytics Service is running");
    }
}
