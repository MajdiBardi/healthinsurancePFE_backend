#!/usr/bin/env python3
"""
Test simple pour identifier le problème de validation
"""

import requests
import json

def test_simple_prediction():
    """Test avec des données minimales"""
    
    # Test 1: Données minimales
    minimal_data = {
        "capital_initial": 1000.0,
        "rendement_annuel": 1.0,
        "duree_contrat_jours": 365,
        "revenu_annuel": 1000.0,
        "score_risque": 0.1,
        "age_client": 25,
        "nb_transactions": 0,
        "montant_versements": 0.0,
        "montant_rachats": 0.0,
        "ratio_rachats": 0.0,
        "nb_alertes": 0,
        "nb_alertes_eleve": 0
    }
    
    print("🧪 Test avec données minimales...")
    print(json.dumps(minimal_data, indent=2))
    
    try:
        response = requests.post(
            "http://localhost:8086/api/ml/prediction/rachat-anticipe",
            json=minimal_data,
            headers={"Content-Type": "application/json"},
            timeout=10
        )
        
        print(f"\n📊 Statut HTTP: {response.status_code}")
        
        if response.status_code == 200:
            result = response.json()
            print("✅ Succès !")
            print(json.dumps(result, indent=2, ensure_ascii=False))
        else:
            print("❌ Erreur !")
            print(f"Réponse: {response.text}")
            
    except Exception as e:
        print(f"💥 Erreur: {e}")

if __name__ == "__main__":
    test_simple_prediction()
