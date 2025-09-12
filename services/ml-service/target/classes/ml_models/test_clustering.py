#!/usr/bin/env python3
"""
Script de test pour vérifier que le modèle de clustering fonctionne
"""

import json
import sys
import os

# Ajouter le répertoire courant au path pour importer les modules
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from ml_clustering import predict_cluster

def test_clustering():
    """Test du modèle de clustering avec des données d'exemple"""
    
    print("🧪 Test du modèle de clustering...")
    
    # Données de test pour différents types de clients
    test_cases = [
        {
            "name": "Client prudent",
            "data": {
                "capital_initial": 35000.0,
                "rendement_annuel": 3.5,
                "revenu_annuel": 45000.0,
                "score_risque": 0.3,
                "montant_versements": 15000.0,
                "montant_rachats": 500.0,
                "ratio_rachats": 0.03,
                "age_client": 45,
                "nb_transactions": 2,
                "nb_alertes": 1,
                "nb_alertes_eleve": 0,
                "type_profil_prudent": True,
                "type_profil_équilibré": False
            }
        },
        {
            "name": "Client jeune équilibré",
            "data": {
                "capital_initial": 25000.0,
                "rendement_annuel": 4.0,
                "revenu_annuel": 55000.0,
                "score_risque": 0.4,
                "montant_versements": 12000.0,
                "montant_rachats": 300.0,
                "ratio_rachats": 0.025,
                "age_client": 32,
                "nb_transactions": 4,
                "nb_alertes": 1,
                "nb_alertes_eleve": 0,
                "type_profil_prudent": False,
                "type_profil_équilibré": True
            }
        },
        {
            "name": "Client à haut capital",
            "data": {
                "capital_initial": 120000.0,
                "rendement_annuel": 3.8,
                "revenu_annuel": 80000.0,
                "score_risque": 0.35,
                "montant_versements": 25000.0,
                "montant_rachats": 2000.0,
                "ratio_rachats": 0.08,
                "age_client": 50,
                "nb_transactions": 5,
                "nb_alertes": 1,
                "nb_alertes_eleve": 0,
                "type_profil_prudent": False,
                "type_profil_équilibré": False
            }
        },
        {
            "name": "Client à risque",
            "data": {
                "capital_initial": 15000.0,
                "rendement_annuel": 2.5,
                "revenu_annuel": 25000.0,
                "score_risque": 0.8,
                "montant_versements": 5000.0,
                "montant_rachats": 3000.0,
                "ratio_rachats": 0.6,
                "age_client": 35,
                "nb_transactions": 2,
                "nb_alertes": 6,
                "nb_alertes_eleve": 3,
                "type_profil_prudent": False,
                "type_profil_équilibré": False
            }
        }
    ]
    
    for i, test_case in enumerate(test_cases, 1):
        print(f"\n📊 Test {i}: {test_case['name']}")
        print("Données d'entrée :")
        print(json.dumps(test_case['data'], indent=2, ensure_ascii=False))
        
        try:
            result = predict_cluster(test_case['data'])
            
            print(f"\n🎯 Résultat du clustering :")
            print(json.dumps(result, indent=2, ensure_ascii=False))
            
            if result["success"]:
                print(f"✅ Test {i} réussi !")
                print(f"   - Cluster assigné : {result['cluster_id']}")
                print(f"   - Nom du cluster : {result['nom_cluster']}")
                print(f"   - Score d'affinité : {result['score_affinite']:.2f}")
            else:
                print(f"❌ Test {i} échoué ! Erreur :", result.get("error", "Inconnue"))
                
        except Exception as e:
            print(f"💥 Erreur lors du test {i} : {str(e)}")

if __name__ == "__main__":
    test_clustering()
