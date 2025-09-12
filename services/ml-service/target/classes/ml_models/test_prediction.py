#!/usr/bin/env python3
"""
Script de test pour vérifier que le modèle de prédiction fonctionne
"""

import json
import sys
import os

# Ajouter le répertoire courant au path pour importer les modules
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from ml_predictor import predict_contract_termination

def test_prediction():
    """Test du modèle de prédiction avec des données d'exemple"""
    
    print("🧪 Test du modèle de prédiction...")
    
    # Données de test basées sur votre notebook
    test_data = {
        "capital_initial": 25000.0,
        "rendement_annuel": 2.5,
        "duree_contrat_jours": 3650,
        "revenu_annuel": 40000.0,
        "score_risque": 0.4,
        "age_client": 45,
        "nb_transactions": 3,
        "montant_versements": 12000.0,
        "montant_rachats": 500.0,
        "ratio_rachats": 0.04,
        "nb_alertes": 1,
        "nb_alertes_eleve": 0,
        "type_profil_prudent": False,
        "type_profil_équilibré": False
    }
    
    print("📊 Données d'entrée :")
    print(json.dumps(test_data, indent=2, ensure_ascii=False))
    
    try:
        result = predict_contract_termination(test_data)
        
        print("\n🎯 Résultat de la prédiction :")
        print(json.dumps(result, indent=2, ensure_ascii=False))
        
        if result["success"]:
            print("\n✅ Test réussi ! Le modèle fonctionne correctement.")
            print(f"   - Rachat anticipé prédit : {result['rachat_anticipe']}")
            print(f"   - Probabilité : {result['probabilite_rachat']:.2%}")
            print(f"   - Niveau de risque : {result['niveau_risque']}")
        else:
            print("\n❌ Test échoué ! Erreur :", result.get("error", "Inconnue"))
            
    except Exception as e:
        print(f"\n💥 Erreur lors du test : {str(e)}")

if __name__ == "__main__":
    test_prediction()
