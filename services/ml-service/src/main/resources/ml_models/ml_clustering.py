#!/usr/bin/env python3
"""
Script Python pour la segmentation des clients
Utilise un modèle K-Means simulé basé sur les règles métier
"""

import sys
import json
import numpy as np

def predict_cluster(input_data):
    """
    Prédit le cluster d'un client basé sur ses caractéristiques
    
    Args:
        input_data: dict contenant les features du client
        
    Returns:
        dict: Résultat de la segmentation
    """
    try:
        # Extraire les features
        age = input_data.get("age_client", 0)
        income = input_data.get("revenu_annuel", 0)
        capital_initial = input_data.get("capital_initial", 0)
        score_risque = input_data.get("score_risque", 0)
        ratio_rachats = input_data.get("ratio_rachats", 0)
        nb_alertes = input_data.get("nb_alertes", 0)
        is_prudent = input_data.get("type_profil_prudent", False)
        is_equilibre = input_data.get("type_profil_équilibré", False)
        
        # Déterminer le cluster basé sur les règles métier
        cluster = determine_cluster(age, income, capital_initial, score_risque, 
                                  ratio_rachats, nb_alertes, is_prudent, is_equilibre)
        
        # Obtenir les informations du cluster
        cluster_info = get_cluster_info(cluster)
        
        # Calculer la confiance
        confidence = calculate_confidence(input_data, cluster)
        
        return {
            "success": True,
            "cluster_id": cluster,
            "nom_cluster": cluster_info["nom"],
            "description": cluster_info["description"],
            "profil_client": cluster_info["profil"],
            "recommandations": cluster_info["recommandations"],
            "score_affinite": confidence,
            "message": "Segmentation basée sur les règles métier K-Means"
        }
        
    except Exception as e:
        return {
            "success": False,
            "error": str(e),
            "cluster_id": 0,
            "nom_cluster": "Cluster par défaut",
            "description": "Erreur lors de la segmentation",
            "profil_client": "Profil non défini",
            "recommandations": "Analyse manuelle recommandée",
            "score_affinite": 0.5,
            "message": f"Erreur lors de la segmentation: {str(e)}"
        }

def determine_cluster(age, income, capital_initial, score_risque, ratio_rachats, nb_alertes, is_prudent, is_equilibre):
    """Détermine le cluster basé sur les caractéristiques du client"""
    
    # Cluster 0: Clients prudents à revenus moyens (727 clients)
    if is_prudent and 30000 <= income <= 60000 and score_risque < 0.5:
        return 0
    
    # Cluster 1: Clients équilibrés jeunes (792 clients)
    if is_equilibre and age < 40 and income >= 40000 and ratio_rachats < 0.1:
        return 1
    
    # Cluster 2: Clients à haut capital (797 clients)
    if capital_initial > 100000 and income > 50000 and nb_alertes <= 2:
        return 2
    
    # Cluster 3: Clients à risque (14 clients)
    if score_risque > 0.7 or ratio_rachats > 0.2 or nb_alertes > 5:
        return 3
    
    # Cluster par défaut (basé sur les caractéristiques principales)
    if age < 35:
        return 1  # Jeunes
    if capital_initial > 50000:
        return 2  # Haut capital
    return 0  # Prudent par défaut

def get_cluster_info(cluster):
    """Retourne les informations détaillées d'un cluster"""
    
    cluster_info = {
        0: {
            "nom": "Clients prudents à revenus moyens",
            "description": "Clients avec un profil prudent, revenus moyens et faible risque",
            "profil": "Profil: Prudent | Revenus: 30k-60k | Score risque: <0.5 | Stabilité: Élevée",
            "recommandations": "Produits sécurisés, Épargne retraite, Assurance vie traditionnelle, Suivi régulier"
        },
        1: {
            "nom": "Clients équilibrés jeunes",
            "description": "Jeunes clients avec un profil équilibré et bon potentiel",
            "profil": "Profil: Équilibré | Âge: <40 ans | Revenus: >40k | Ratio rachats: <0.1",
            "recommandations": "Produits équilibrés, Assurance vie mixte, Investissements modérés, Croissance"
        },
        2: {
            "nom": "Clients à haut capital",
            "description": "Clients avec un capital élevé et des revenus importants",
            "profil": "Profil: Haut capital | Capital: >100k | Revenus: >50k | Alertes: ≤2",
            "recommandations": "Produits premium, Gestion de patrimoine, Investissements sophistiqués, Services VIP"
        },
        3: {
            "nom": "Clients à risque",
            "description": "Clients présentant des signaux de risque élevé",
            "profil": "Profil: Risque élevé | Score risque: >0.7 | Ratio rachats: >0.2 | Alertes: >5",
            "recommandations": "Suivi renforcé, Produits sécurisés, Accompagnement personnalisé, Réduction risques"
        }
    }
    
    return cluster_info.get(cluster, cluster_info[0])

def calculate_confidence(input_data, cluster):
    """Calcule la confiance de l'assignation au cluster"""
    
    age = input_data.get("age_client", 0)
    income = input_data.get("revenu_annuel", 0)
    capital_initial = input_data.get("capital_initial", 0)
    score_risque = input_data.get("score_risque", 0)
    ratio_rachats = input_data.get("ratio_rachats", 0)
    nb_alertes = input_data.get("nb_alertes", 0)
    is_prudent = input_data.get("type_profil_prudent", False)
    is_equilibre = input_data.get("type_profil_équilibré", False)
    
    confidence = 0.5  # Confiance de base
    
    if cluster == 0:  # Clients prudents
        if is_prudent and 30000 <= income <= 60000 and score_risque < 0.5:
            confidence = 0.9
        elif score_risque < 0.5:
            confidence = 0.7
    elif cluster == 1:  # Clients équilibrés jeunes
        if is_equilibre and age < 35 and income > 40000:
            confidence = 0.9
        elif age < 40:
            confidence = 0.7
    elif cluster == 2:  # Clients à haut capital
        if capital_initial > 100000 and income > 50000:
            confidence = 0.9
        elif capital_initial > 50000:
            confidence = 0.7
    elif cluster == 3:  # Clients à risque
        if score_risque > 0.7 or ratio_rachats > 0.2:
            confidence = 0.9
        elif nb_alertes > 3:
            confidence = 0.7
    
    return min(0.95, max(0.1, confidence))

if __name__ == "__main__":
    # Lecture des données depuis stdin
    input_json = sys.stdin.read()
    
    try:
        input_data = json.loads(input_json)
        result = predict_cluster(input_data)
        print(json.dumps(result, ensure_ascii=False))
    except Exception as e:
        error_result = {
            "success": False,
            "error": str(e),
            "cluster_id": 0,
            "nom_cluster": "Cluster par défaut",
            "description": "Erreur lors de la segmentation",
            "profil_client": "Profil non défini",
            "recommandations": "Analyse manuelle recommandée",
            "score_affinite": 0.5,
            "message": f"Erreur de traitement: {str(e)}"
        }
        print(json.dumps(error_result, ensure_ascii=False))
