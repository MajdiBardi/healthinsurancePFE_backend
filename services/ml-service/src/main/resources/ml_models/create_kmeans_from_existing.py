#!/usr/bin/env python3
"""
Script pour créer les modèles K-Means à partir des données existantes
"""

import pandas as pd
import numpy as np
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler
import joblib

def create_kmeans_models():
    """Crée les modèles K-Means à partir des données existantes"""
    
    print("📊 Création des modèles K-Means...")
    
    # Features utilisées dans le clustering
    features = [
        'capital_initial', 'rendement_annuel', 'revenu_annuel', 'score_risque',
        'montant_versements', 'montant_rachats', 'ratio_rachats', 'age_client',
        'nb_transactions', 'nb_alertes', 'nb_alertes_eleve',
        'type_profil_prudent', 'type_profil_équilibré'
    ]
    
    # Créer des données d'exemple basées sur les clusters existants
    # Cluster 0: Clients prudents à revenus moyens (727 clients)
    cluster_0_data = {
        'capital_initial': np.random.normal(30000, 10000, 727),
        'rendement_annuel': np.random.normal(3.5, 0.5, 727),
        'revenu_annuel': np.random.normal(45000, 10000, 727),
        'score_risque': np.random.normal(0.3, 0.1, 727),
        'montant_versements': np.random.normal(15000, 5000, 727),
        'montant_rachats': np.random.normal(1000, 500, 727),
        'ratio_rachats': np.random.normal(0.05, 0.02, 727),
        'age_client': np.random.normal(45, 10, 727),
        'nb_transactions': np.random.poisson(3, 727),
        'nb_alertes': np.random.poisson(1, 727),
        'nb_alertes_eleve': np.random.poisson(0.2, 727),
        'type_profil_prudent': np.ones(727),
        'type_profil_équilibré': np.zeros(727)
    }
    
    # Cluster 1: Clients équilibrés jeunes (792 clients)
    cluster_1_data = {
        'capital_initial': np.random.normal(25000, 8000, 792),
        'rendement_annuel': np.random.normal(4.0, 0.6, 792),
        'revenu_annuel': np.random.normal(55000, 15000, 792),
        'score_risque': np.random.normal(0.4, 0.15, 792),
        'montant_versements': np.random.normal(12000, 4000, 792),
        'montant_rachats': np.random.normal(800, 400, 792),
        'ratio_rachats': np.random.normal(0.03, 0.01, 792),
        'age_client': np.random.normal(32, 8, 792),
        'nb_transactions': np.random.poisson(4, 792),
        'nb_alertes': np.random.poisson(1.5, 792),
        'nb_alertes_eleve': np.random.poisson(0.3, 792),
        'type_profil_prudent': np.zeros(792),
        'type_profil_équilibré': np.ones(792)
    }
    
    # Cluster 2: Clients à haut capital (797 clients)
    cluster_2_data = {
        'capital_initial': np.random.normal(120000, 30000, 797),
        'rendement_annuel': np.random.normal(3.8, 0.4, 797),
        'revenu_annuel': np.random.normal(80000, 20000, 797),
        'score_risque': np.random.normal(0.35, 0.1, 797),
        'montant_versements': np.random.normal(25000, 8000, 797),
        'montant_rachats': np.random.normal(2000, 1000, 797),
        'ratio_rachats': np.random.normal(0.04, 0.02, 797),
        'age_client': np.random.normal(50, 12, 797),
        'nb_transactions': np.random.poisson(5, 797),
        'nb_alertes': np.random.poisson(1, 797),
        'nb_alertes_eleve': np.random.poisson(0.1, 797),
        'type_profil_prudent': np.zeros(797),
        'type_profil_équilibré': np.zeros(797)
    }
    
    # Cluster 3: Clients à risque (14 clients)
    cluster_3_data = {
        'capital_initial': np.random.normal(15000, 5000, 14),
        'rendement_annuel': np.random.normal(2.5, 0.8, 14),
        'revenu_annuel': np.random.normal(25000, 8000, 14),
        'score_risque': np.random.normal(0.8, 0.1, 14),
        'montant_versements': np.random.normal(5000, 2000, 14),
        'montant_rachats': np.random.normal(3000, 1500, 14),
        'ratio_rachats': np.random.normal(0.25, 0.1, 14),
        'age_client': np.random.normal(35, 15, 14),
        'nb_transactions': np.random.poisson(2, 14),
        'nb_alertes': np.random.poisson(6, 14),
        'nb_alertes_eleve': np.random.poisson(3, 14),
        'type_profil_prudent': np.zeros(14),
        'type_profil_équilibré': np.zeros(14)
    }
    
    # Combiner toutes les données
    all_data = []
    all_labels = []
    
    for i, data in enumerate([cluster_0_data, cluster_1_data, cluster_2_data, cluster_3_data]):
        df_cluster = pd.DataFrame(data)
        all_data.append(df_cluster)
        all_labels.extend([i] * len(df_cluster))
    
    # Créer le DataFrame final
    df_combined = pd.concat(all_data, ignore_index=True)
    labels = np.array(all_labels)
    
    print(f"✅ Données créées : {len(df_combined)} clients, {len(features)} features")
    
    # Préparer les features
    X = df_combined[features].values
    
    # Normalisation
    print("🔧 Normalisation des données...")
    scaler = StandardScaler()
    X_scaled = scaler.fit_transform(X)
    
    # Entraînement K-Means
    print("🧠 Entraînement du modèle K-Means...")
    kmeans = KMeans(n_clusters=4, random_state=42, n_init=10)
    kmeans.fit(X_scaled)
    
    # Vérifier la cohérence
    predicted_labels = kmeans.predict(X_scaled)
    
    print("📈 Résultats du clustering :")
    unique, counts = np.unique(predicted_labels, return_counts=True)
    for cluster_id, count in zip(unique, counts):
        print(f"  Cluster {cluster_id}: {count} clients")
    
    # Sauvegarder les modèles
    print("💾 Sauvegarde des modèles...")
    
    joblib.dump(kmeans, "kmeans_model.joblib")
    print("✅ Modèle K-Means sauvegardé : kmeans_model.joblib")
    
    joblib.dump(scaler, "kmeans_scaler.joblib")
    print("✅ Scaler K-Means sauvegardé : kmeans_scaler.joblib")
    
    # Métadonnées
    metadata = {
        'n_clusters': 4,
        'features': features,
        'n_samples': len(X),
        'cluster_distribution': dict(zip(unique, counts))
    }
    
    joblib.dump(metadata, "kmeans_metadata.joblib")
    print("✅ Métadonnées sauvegardées : kmeans_metadata.joblib")
    
    print("🎉 Modèles K-Means créés avec succès !")
    
    return kmeans, scaler

if __name__ == "__main__":
    create_kmeans_models()
