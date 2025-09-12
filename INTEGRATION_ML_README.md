# 🤖 Intégration Machine Learning - PFE

## Vue d'ensemble

Ce document décrit l'intégration des modèles de Machine Learning développés dans le notebook `PFE_ML.ipynb` dans l'architecture microservices Spring Boot + React.

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Gateway       │    │   ML Service    │
│   React         │◄──►│   Spring        │◄──►│   Spring Boot   │
│                 │    │                 │    │                 │
│ - Dashboard ML  │    │ - Routing       │    │ - Prédictions   │
│ - Analytics     │    │ - Auth          │    │ - Clustering    │
│ - Visualizations│    │ - Load Balance  │    │ - APIs REST     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │   Database      │
                       │   PostgreSQL    │
                       │                 │
                       │ - Données       │
                       │ - Modèles       │
                       └─────────────────┘
```

## 🎯 Modèles ML Intégrés

### 1. Modèle de Prédiction des Rachats Anticipés
- **Algorithme**: Régression Logistique
- **Performance**: 72% accuracy, 80% ROC AUC
- **Objectif**: Prédire si un client va effectuer un rachat anticipé
- **Features**: 15 variables (capital, rendement, âge, transactions, etc.)

### 2. Modèle de Segmentation Client (Clustering)
- **Algorithme**: K-Means (4 clusters)
- **Performance**: Silhouette Score 0.151
- **Objectif**: Segmenter les clients en profils d'investissement
- **Clusters**: Prudents, Équilibrés, Agressifs, VIP

## 🚀 Démarrage Rapide

### 1. Démarrer les Services

```bash
# Démarrer l'infrastructure (PostgreSQL, etc.)
docker-compose up -d

# Démarrer le service ML
./start_ml_service.bat
```

### 2. Tester les APIs

Ouvrez le fichier `test_ml_apis.html` dans votre navigateur pour tester toutes les fonctionnalités ML.

### 3. Accéder au Frontend

- **Dashboard Principal**: http://localhost:3000/dashboard
- **Analytics ML**: http://localhost:3000/ml-analytics

## 📡 APIs Disponibles

### Service ML (Port 8086)

#### Prédiction de Rachat Anticipé
```http
POST /api/ml/prediction/rachat-anticipe
Content-Type: application/json

{
  "capitalInitial": 25000,
  "rendementAnnuel": 2.5,
  "dureeContratJours": 3650,
  "revenuAnnuel": 40000,
  "scoreRisque": 0.4,
  "ageClient": 45,
  "nbTransactions": 3,
  "montantVersements": 12000,
  "montantRachats": 500,
  "ratioRachats": 0.04,
  "nbAlertes": 1,
  "nbAlertesEleve": 0,
  "typeProfilPrudent": false,
  "typeProfilEquilibre": false
}
```

**Réponse:**
```json
{
  "rachatAnticipe": true,
  "probabiliteRachat": 0.7342,
  "niveauRisque": "ÉLEVÉ",
  "recommandation": "Attention requise, risque élevé de rachat anticipé",
  "message": "Probabilité de rachat anticipé: 73.42%"
}
```

#### Segmentation Client
```http
POST /api/ml/clustering/segment-client
Content-Type: application/json

{
  "capitalInitial": 50000,
  "rendementAnnuel": 3.2,
  "revenuAnnuel": 60000,
  "scoreRisque": 0.6,
  "montantVersements": 15000,
  "montantRachats": 2000,
  "ratioRachats": 0.13,
  "ageClient": 38,
  "nbTransactions": 8,
  "nbAlertes": 2,
  "nbAlertesEleve": 1,
  "typeProfilPrudent": false,
  "typeProfilEquilibre": true
}
```

**Réponse:**
```json
{
  "clusterId": 1,
  "nomCluster": "Équilibrés",
  "description": "Clients avec un profil de risque modéré et une approche équilibrée",
  "profilClient": "Investisseur équilibré, recherche un compromis risque/rendement",
  "recommandations": "Proposer des produits mixtes et des stratégies diversifiées",
  "scoreAffinite": 0.80
}
```

#### Analytics Dashboard
```http
GET /api/ml/analytics/dashboard
```

**Réponse:**
```json
{
  "predictionMetrics": {
    "accuracy": 0.72,
    "f1Score": 0.36,
    "rocAuc": 0.80,
    "totalPredictions": 1250,
    "predictionsToday": 45
  },
  "clusteringMetrics": {
    "totalClusters": 4,
    "silhouetteScore": 0.151,
    "totalClients": 2330
  },
  "clusterDistribution": {
    "Prudents": 727,
    "Équilibrés": 792,
    "Agressifs": 797,
    "VIP": 14
  },
  "predictionDistribution": {
    "Rachat Anticipé": 325,
    "Non Rachat": 2675
  }
}
```

## 🎨 Interface Utilisateur

### Dashboard Principal
- Widget ML avec métriques en temps réel
- Intégration avec Power BI
- Navigation vers Analytics ML

### Page Analytics ML
- **Prédictions**: Formulaire interactif pour tester les prédictions
- **Segmentation**: Interface pour segmenter les clients
- **Métriques**: Tableaux de bord avec visualisations
- **Performance**: Monitoring des modèles

### Composants React
- `MlMetricsWidget`: Widget pour le dashboard principal
- `MlAnalyticsPage`: Page complète d'analytics ML
- Intégration dans la navigation sidebar

## 🔧 Configuration

### Service ML (application.yml)
```yaml
server:
  port: 8086

ml:
  models:
    path: classpath:ml_models/
    prediction:
      model-file: logistic_model.joblib
      scaler-file: scaler.joblib
    clustering:
      model-file: kmeans_model.joblib
      scaler-file: clustering_scaler.joblib
```

### Base de Données
- **Port**: 5433 (PostgreSQL ML)
- **Base**: DW_Assurance
- **Utilisateur**: postgres
- **Mot de passe**: 191jMT1019MB..

## 📁 Structure des Fichiers

```
services/ml-service/
├── src/main/java/com/pfe/mlservice/
│   ├── MlServiceApplication.java
│   ├── controllers/
│   │   ├── MlPredictionController.java
│   │   ├── MlClusteringController.java
│   │   └── MlAnalyticsController.java
│   ├── services/
│   │   ├── MlPredictionService.java
│   │   └── MlClusteringService.java
│   └── dtos/
│       ├── PredictionRequestDto.java
│       ├── PredictionResponseDto.java
│       ├── ClusteringRequestDto.java
│       └── ClusteringResponseDto.java
├── src/main/resources/
│   ├── application.yml
│   └── ml_models/
│       ├── logistic_model.joblib
│       ├── scaler.joblib
│       └── ml_predictor.py
└── pom.xml

material-kit-react-main/src/
├── app/
│   ├── dashboard/page.tsx
│   └── ml-analytics/page.tsx
└── components/dashboard/
    └── MlMetricsWidget.tsx
```

## 🧪 Tests

### Test des APIs
1. Ouvrir `test_ml_apis.html` dans le navigateur
2. Tester la prédiction avec des données d'exemple
3. Tester la segmentation client
4. Vérifier les analytics

### Test du Frontend
1. Démarrer le service React: `npm run dev`
2. Naviguer vers `/dashboard` pour voir le widget ML
3. Aller sur `/ml-analytics` pour les fonctionnalités complètes

## 🔍 Monitoring

### Health Checks
- **Service ML**: http://localhost:8086/actuator/health
- **Prédiction**: http://localhost:8086/api/ml/prediction/health
- **Clustering**: http://localhost:8086/api/ml/clustering/health
- **Analytics**: http://localhost:8086/api/ml/analytics/health

### Métriques
- Accuracy, F1-Score, ROC AUC
- Nombre de prédictions par jour
- Répartition des clusters
- Performance des modèles

## 🚨 Dépannage

### Problèmes Courants

1. **Service ML ne démarre pas**
   - Vérifier que Python est installé
   - Vérifier que les modèles .joblib sont présents
   - Vérifier les logs: `mvnw spring-boot:run`

2. **Erreurs de prédiction**
   - Vérifier le format des données d'entrée
   - Vérifier que le scaler est compatible
   - Vérifier les logs Python

3. **Frontend ne se connecte pas**
   - Vérifier que le service ML est sur le port 8086
   - Vérifier CORS dans les contrôleurs
   - Vérifier la console du navigateur

### Logs
```bash
# Logs du service ML
tail -f services/ml-service/target/spring.log

# Logs Docker
docker-compose logs -f postgres-ml
```

## 🔮 Évolutions Futures

1. **Nouveaux Modèles**
   - Détection de fraude
   - Prédiction de churn
   - Recommandation de produits

2. **Améliorations**
   - Cache des prédictions
   - Batch processing
   - Real-time streaming

3. **Monitoring Avancé**
   - Métriques Prometheus
   - Alertes automatiques
   - A/B testing des modèles

## 📞 Support

Pour toute question ou problème:
1. Vérifier les logs des services
2. Tester avec `test_ml_apis.html`
3. Consulter la documentation des APIs
4. Vérifier la configuration des modèles

---

**🎉 Félicitations !** Votre intégration ML est maintenant opérationnelle et prête à fournir des insights intelligents à votre application d'assurance.
