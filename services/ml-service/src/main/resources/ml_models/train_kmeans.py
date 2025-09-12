#!/usr/bin/env python3
"""
Script pour entraîner et sauvegarder le modèle K-Means
Basé sur les données clients.csv
"""

import pandas as pd
import numpy as np
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler
import joblib
import os

def train_kmeans_model():
    """Entraîne et sauvegarde le modèle K-Means"""
    
    # Chemin vers les données
    data_path = r"C:\Users\user\Desktop\streamlit\data\clients.csv"
    
    # Charger les données
    print("📊 Chargement des données...")
    df = pd.read_csv(data_path)
    print(f"✅ Données chargées : {df.shape[0]} clients, {df.shape[1]} features")
    
    # Features pour le clustering (mêmes que dans le notebook)
    features = [
        'capital_initial', 'rendement_annuel', 'revenu_annuel', 'score_risque',
        'montant_versements', 'montant_rachats', 'ratio_rachats', 'age_client',
        'nb_transactions', 'nb_alertes', 'nb_alertes_eleve',
        'type_profil_prudent', 'type_profil_équilibré'
    ]
    
    # Préparer les données
    X = df[features].copy()
    
    # Nettoyage des données
    for col in features:
        X[col] = pd.to_numeric(X[col], errors='coerce')
    
    # Remplacer les NaN par 0
    X = X.fillna(0)
    
    print("🔧 Normalisation des données...")
    # Normalisation
    scaler = StandardScaler()
    X_scaled = scaler.fit_transform(X)
    
    print("🧠 Entraînement du modèle K-Means...")
    # Entraînement K-Means avec 4 clusters (comme dans le notebook)
    kmeans = KMeans(n_clusters=4, random_state=42, n_init=10)
    kmeans.fit(X_scaled)
    
    # Prédictions
    labels = kmeans.predict(X_scaled)
    
    print("📈 Résultats du clustering :")
    unique, counts = np.unique(labels, return_counts=True)
    for cluster_id, count in zip(unique, counts):
        print(f"  Cluster {cluster_id}: {count} clients")
    
    # Sauvegarder les modèles
    print("💾 Sauvegarde des modèles...")
    
    # Sauvegarder le modèle K-Means
    joblib.dump(kmeans, "kmeans_model.joblib")
    print("✅ Modèle K-Means sauvegardé : kmeans_model.joblib")
    
    # Sauvegarder le scaler
    joblib.dump(scaler, "kmeans_scaler.joblib")
    print("✅ Scaler K-Means sauvegardé : kmeans_scaler.joblib")
    
    # Créer un fichier de métadonnées
    metadata = {
        'n_clusters': 4,
        'features': features,
        'n_samples': len(X),
        'cluster_distribution': dict(zip(unique, counts))
    }
    
    joblib.dump(metadata, "kmeans_metadata.joblib")
    print("✅ Métadonnées sauvegardées : kmeans_metadata.joblib")
    
    print("🎉 Entraînement terminé avec succès !")
    
    return kmeans, scaler, labels

if __name__ == "__main__":
    train_kmeans_model()
