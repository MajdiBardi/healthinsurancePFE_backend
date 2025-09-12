#!/usr/bin/env python3
"""
Script Python pour la prédiction de rachats anticipés
Utilise le modèle Logistic Regression entraîné
"""

import sys
import json
import joblib
import numpy as np

def predict_contract_termination(input_data):
    """
    Prédit si un contrat sera racheté de manière anticipée
    
    Args:
        input_data: dict contenant les features du contrat
        
    Returns:
        dict: Résultat de la prédiction
    """
    try:
        # Charger le modèle et le scaler
        model = joblib.load("logistic_model.joblib")
        scaler = joblib.load("scaler.joblib")
        
        # Préparer les features dans le bon ordre
        features = [
            "capital_initial", "rendement_annuel", "duree_contrat_jours",
            "revenu_annuel", "score_risque", "age_client",
            "nb_transactions", "montant_versements", "montant_rachats", "ratio_rachats",
            "nb_alertes", "nb_alertes_eleve", "type_profil_prudent", "type_profil_equilibre"
        ]
        
        # Créer le vecteur de features
        X = np.array([[input_data.get(feature, 0) for feature in features]])
        
        # Normaliser les données
        X_scaled = scaler.transform(X)
        
        # Prédiction
        prediction = model.predict(X_scaled)[0]
        probability = model.predict_proba(X_scaled)[0][1]  # Probabilité de rachat anticipé
        
        # Déterminer le niveau de risque
        if probability < 0.3:
            risk_level = "Risque faible"
        elif probability < 0.6:
            risk_level = "Risque moyen"
        elif probability < 0.8:
            risk_level = "Risque élevé"
        else:
            risk_level = "Risque très élevé"
        
        # Générer des recommandations
        if prediction == 1:
            if probability > 0.8:
                recommendation = "Rachat anticipé très probable - Proposer des alternatives ou révision du contrat"
            else:
                recommendation = "Rachat anticipé probable - Surveiller le client et proposer des ajustements"
        else:
            if probability < 0.3:
                recommendation = "Client stable - Maintenir la relation et proposer des produits complémentaires"
            else:
                recommendation = "Client à surveiller - Renforcer la communication et l'accompagnement"
        
        return {
            "success": True,
            "rachat_anticipe": bool(prediction),
            "probabilite_rachat": float(probability),
            "niveau_risque": risk_level,
            "recommandation": recommendation,
            "message": "Prédiction basée sur le modèle Logistic Regression entraîné"
        }
        
    except Exception as e:
        return {
            "success": False,
            "error": str(e),
            "rachat_anticipe": False,
            "probabilite_rachat": 0.5,
            "niveau_risque": "Risque moyen",
            "recommandation": "Analyse manuelle recommandée",
            "message": f"Erreur lors de la prédiction: {str(e)}"
        }

if __name__ == "__main__":
    # Lecture des données depuis stdin
    input_json = sys.stdin.read()
    
    try:
        input_data = json.loads(input_json)
        result = predict_contract_termination(input_data)
        print(json.dumps(result, ensure_ascii=False))
    except Exception as e:
        error_result = {
            "success": False,
            "error": str(e),
            "rachat_anticipe": False,
            "probabilite_rachat": 0.5,
            "niveau_risque": "Risque moyen",
            "recommandation": "Analyse manuelle recommandée",
            "message": f"Erreur de traitement: {str(e)}"
        }
        print(json.dumps(error_result, ensure_ascii=False))