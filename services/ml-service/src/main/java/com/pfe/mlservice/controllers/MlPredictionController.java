package com.pfe.mlservice.controllers;

import com.pfe.mlservice.dtos.PredictionRequestDto;
import com.pfe.mlservice.dtos.PredictionResponseDto;
import com.pfe.mlservice.services.MlPredictionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ml/prediction")
@CrossOrigin(origins = "*")
public class MlPredictionController {

    @Autowired
    private MlPredictionService mlPredictionService;

    @PostMapping("/rachat-anticipe")
    public ResponseEntity<PredictionResponseDto> predictRachatAnticipe(
            @Valid @RequestBody PredictionRequestDto request) {
        try {
            PredictionResponseDto response = mlPredictionService.predictContractTermination(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // En cas d'erreur, retourner une réponse par défaut
            PredictionResponseDto errorResponse = new PredictionResponseDto(
                false, 0.0, "ERREUR", 
                "Erreur lors de la prédiction", 
                "Erreur: " + e.getMessage()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("ML Prediction Service is running");
    }
}
