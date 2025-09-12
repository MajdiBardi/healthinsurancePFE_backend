package com.pfe.mlservice.controllers;

import com.pfe.mlservice.dtos.ClusteringRequestDto;
import com.pfe.mlservice.dtos.ClusteringResponseDto;
import com.pfe.mlservice.services.MlClusteringService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ml/clustering")
@CrossOrigin(origins = "*")
public class MlClusteringController {

    @Autowired
    private MlClusteringService mlClusteringService;

    @PostMapping("/segment-client")
    public ResponseEntity<ClusteringResponseDto> segmentClient(
            @Valid @RequestBody ClusteringRequestDto request) {
        try {
            ClusteringResponseDto response = mlClusteringService.predictCluster(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // En cas d'erreur, retourner une réponse par défaut
            ClusteringResponseDto errorResponse = new ClusteringResponseDto(
                -1, "ERREUR", 
                "Erreur lors du clustering", 
                "Non déterminé", 
                "Contactez le support technique", 
                0.0
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/clusters-info")
    public ResponseEntity<String> getClustersInfo() {
        String clustersInfo = """
            {
                "clusters": [
                    {
                        "id": 0,
                        "nom": "Prudents",
                        "description": "Clients avec un profil de risque faible et des investissements conservateurs",
                        "caracteristiques": ["Faible score de risque", "Peu de rachats", "Investissements sécurisés"]
                    },
                    {
                        "id": 1,
                        "nom": "Équilibrés", 
                        "description": "Clients avec un profil de risque modéré et une approche équilibrée",
                        "caracteristiques": ["Risque modéré", "Approche équilibrée", "Diversification"]
                    },
                    {
                        "id": 2,
                        "nom": "Agressifs",
                        "description": "Clients avec un profil de risque élevé et des transactions fréquentes", 
                        "caracteristiques": ["Haut risque", "Transactions fréquentes", "Recherche de rendement"]
                    },
                    {
                        "id": 3,
                        "nom": "VIP",
                        "description": "Clients à fort potentiel avec des investissements importants",
                        "caracteristiques": ["Fort capital", "Revenus élevés", "Service premium"]
                    }
                ]
            }
            """;
        return ResponseEntity.ok(clustersInfo);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("ML Clustering Service is running");
    }
}
