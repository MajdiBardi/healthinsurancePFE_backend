#!/usr/bin/env python3
"""
Script de test pour vérifier si les vrais modèles sont utilisés
"""

import requests
import json
import time

def test_ml_service():
    """Test complet du service ML"""
    
    base_url = "http://localhost:8086"
    
    print("🧪 Test du Service ML - Vérification des Vrais Modèles")
    print("=" * 60)
    
    # Test 1: Santé du service
    print("\n1. 🏥 Test de santé du service...")
    try:
        response = requests.get(f"{base_url}/api/ml/prediction/health", timeout=5)
        if response.status_code == 200:
            print("✅ Service ML accessible")
        else:
            print(f"❌ Service ML inaccessible (HTTP {response.status_code})")
            return
    except requests.exceptions.RequestException as e:
        print(f"❌ Service ML inaccessible: {e}")
        print("💡 Assurez-vous que le service ML est démarré sur le port 8086")
        return
    
    # Test 2: Prédiction avec données d'exemple
    print("\n2. 🔮 Test de prédiction...")
    
    prediction_data = {
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
        "type_profil_equilibre": False
    }
    
    try:
        response = requests.post(
            f"{base_url}/api/ml/prediction/rachat-anticipe",
            json=prediction_data,
            headers={"Content-Type": "application/json"},
            timeout=10
        )
        
        if response.status_code == 200:
            result = response.json()
            print("✅ Prédiction réussie")
            print(f"   - Rachat anticipé: {result['rachatAnticipe']}")
            print(f"   - Probabilité: {result['probabiliteRachat']:.2%}")
            print(f"   - Niveau de risque: {result['niveauRisque']}")
            print(f"   - Message: {result['message']}")
            
            # Analyser si c'est le vrai modèle ou le fallback
            if "Logistic Regression" in result['message']:
                print("🎯 ✅ Utilise le VRAI modèle Logistic Regression !")
            elif "fallback" in result['message']:
                print("⚠️ Utilise les règles métier (fallback)")
            else:
                print("❓ Type de modèle indéterminé")
        else:
            print(f"❌ Erreur prédiction (HTTP {response.status_code})")
            print(f"   Réponse: {response.text}")
            
    except requests.exceptions.RequestException as e:
        print(f"❌ Erreur lors de la prédiction: {e}")
    
    # Test 3: Clustering avec données d'exemple
    print("\n3. 🧠 Test de clustering...")
    
    clustering_data = {
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
        "type_profil_equilibre": False
    }
    
    try:
        response = requests.post(
            f"{base_url}/api/ml/clustering/segment-client",
            json=clustering_data,
            headers={"Content-Type": "application/json"},
            timeout=10
        )
        
        if response.status_code == 200:
            result = response.json()
            print("✅ Clustering réussi")
            print(f"   - Cluster ID: {result['clusterId']}")
            print(f"   - Nom du cluster: {result['nomCluster']}")
            print(f"   - Description: {result['description']}")
            print(f"   - Score d'affinité: {result['scoreAffinite']:.2%}")
            
            # Analyser si c'est le vrai modèle ou le fallback
            if "K-Means" in result['description']:
                print("🎯 ✅ Utilise le VRAI modèle K-Means !")
            elif "fallback" in result['description']:
                print("⚠️ Utilise les règles métier (fallback)")
            else:
                print("❓ Type de modèle indéterminé")
        else:
            print(f"❌ Erreur clustering (HTTP {response.status_code})")
            print(f"   Réponse: {response.text}")
            
    except requests.exceptions.RequestException as e:
        print(f"❌ Erreur lors du clustering: {e}")
    
    # Test 4: Test avec différentes données pour voir la cohérence
    print("\n4. 🔄 Test de cohérence avec différentes données...")
    
    test_cases = [
        {"name": "Client à risque élevé", "data": {
            "capital_initial": 10000.0, "rendement_annuel": 2.0, "revenu_annuel": 20000.0,
            "score_risque": 0.9, "montant_versements": 5000.0, "montant_rachats": 2000.0,
            "ratio_rachats": 0.4, "age_client": 30, "nb_transactions": 1, "nb_alertes": 8,
            "nb_alertes_eleve": 5, "type_profil_prudent": False, "type_profil_equilibre": False
        }},
        {"name": "Client à haut capital", "data": {
            "capital_initial": 150000.0, "rendement_annuel": 4.0, "revenu_annuel": 100000.0,
            "score_risque": 0.2, "montant_versements": 30000.0, "montant_rachats": 1000.0,
            "ratio_rachats": 0.03, "age_client": 55, "nb_transactions": 6, "nb_alertes": 0,
            "nb_alertes_eleve": 0, "type_profil_prudent": False, "type_profil_equilibre": False
        }}
    ]
    
    for i, test_case in enumerate(test_cases, 1):
        print(f"\n   Test {i}: {test_case['name']}")
        
        try:
            # Test prédiction
            pred_response = requests.post(
                f"{base_url}/api/ml/prediction/rachat-anticipe",
                json=test_case['data'],
                headers={"Content-Type": "application/json"},
                timeout=5
            )
            
            if pred_response.status_code == 200:
                pred_result = pred_response.json()
                print(f"      Prédiction: {pred_result['rachatAnticipe']} ({pred_result['probabiliteRachat']:.2%})")
            
            # Test clustering
            clust_response = requests.post(
                f"{base_url}/api/ml/clustering/segment-client",
                json=test_case['data'],
                headers={"Content-Type": "application/json"},
                timeout=5
            )
            
            if clust_response.status_code == 200:
                clust_result = clust_response.json()
                print(f"      Cluster: {clust_result['clusterId']} ({clust_result['nomCluster']})")
                
        except requests.exceptions.RequestException as e:
            print(f"      ❌ Erreur: {e}")
    
    print("\n" + "=" * 60)
    print("🎉 Test terminé !")
    print("\n💡 Pour vérifier manuellement:")
    print("   1. Ouvrez le fichier test_ml_api.html dans votre navigateur")
    print("   2. Cliquez sur les boutons de test")
    print("   3. Analysez les messages pour voir si les vrais modèles sont utilisés")

if __name__ == "__main__":
    test_ml_service()
