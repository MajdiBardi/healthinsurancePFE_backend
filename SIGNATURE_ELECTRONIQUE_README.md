# Service de Signature Électronique - Guide Complet

## 🎯 Fonctionnalités Implémentées

### Backend (Spring Boot)
- ✅ **Entité Contract** mise à jour avec champs de signature
- ✅ **Service de signature électronique** avec validation
- ✅ **Génération PDF** avec signatures visuelles (iText)
- ✅ **Endpoints API** pour signer et vérifier les signatures
- ✅ **Notifications** automatiques par email
- ✅ **Logs de débogage** pour diagnostiquer les problèmes

### Frontend (React/Next.js)
- ✅ **Composant de signature** avec canvas HTML5
- ✅ **Interface utilisateur** intuitive pour signer
- ✅ **Affichage du statut** de signature en temps réel
- ✅ **Téléchargement PDF** avec signatures intégrées
- ✅ **Gestion d'erreurs** améliorée avec logs détaillés

## 🚀 Démarrage Rapide

### 1. Préparer la base de données
```sql
-- Exécuter dans PostgreSQL
ALTER TABLE contrats 
ADD COLUMN IF NOT EXISTS client_signature TEXT,
ADD COLUMN IF NOT EXISTS insurer_signature TEXT,
ADD COLUMN IF NOT EXISTS client_signed_at DATE,
ADD COLUMN IF NOT EXISTS insurer_signed_at DATE,
ADD COLUMN IF NOT EXISTS is_fully_signed BOOLEAN DEFAULT FALSE;
```

### 2. Démarrer le service backend
```bash
cd services/contract
mvn spring-boot:run
```

### 3. Démarrer le frontend
```bash
cd material-kit-react-main/material-kit-react-main
npm run dev
```

## 📋 Endpoints API

### Signature Électronique
- `POST /api/contracts/{id}/sign` - Signer un contrat
- `GET /api/contracts/{id}/signature-status` - Statut de signature
- `GET /api/contracts/{id}/test-signature` - Test de l'endpoint
- `GET /api/contracts/{id}/pdf` - Télécharger PDF avec signatures

### Endpoints de fallback
- `POST /api/signatures/sign` - Signature via service dédié
- `GET /api/signatures/contract/{id}/status` - Statut via service dédié

## 🔧 Dépannage

### Erreur 400 - Bad Request
1. **Vérifier la base de données** : Les colonnes de signature existent-elles ?
2. **Vérifier les logs** : Regarder les messages de debug dans la console
3. **Tester l'API** : Utiliser `test_signature_api.html`
4. **Vérifier l'authentification** : Le token JWT est-il valide ?

### Erreur 404 - Not Found
1. **Vérifier que le service est démarré** : Port 8222 accessible ?
2. **Vérifier l'URL** : Utiliser les endpoints de fallback
3. **Vérifier la configuration** : Spring Security configuré ?

### Erreur 500 - Internal Server Error
1. **Vérifier les logs** : Regarder la stack trace complète
2. **Vérifier les dépendances** : iText installé correctement ?
3. **Vérifier la base de données** : Connexion fonctionnelle ?

## 🧪 Tests

### Test Manuel
1. Ouvrir `test_signature_api.html` dans le navigateur
2. Tester chaque endpoint individuellement
3. Vérifier les réponses dans la console

### Test Frontend
1. Aller sur `/contracts`
2. Cliquer sur "Voir détails" d'un contrat
3. Cliquer sur "✍️ Signer"
4. Dessiner une signature et cliquer "Signer le contrat"

## 📁 Fichiers Créés/Modifiés

### Backend
- `entities/Contract.java` - Champs de signature ajoutés
- `services/ElectronicSignatureService.java` - Service de signature
- `services/ElectronicSignatureServiceImpl.java` - Implémentation
- `services/PDFGenerationService.java` - Service PDF
- `services/PDFGenerationServiceImpl.java` - Implémentation PDF
- `controllers/ElectronicSignatureController.java` - Contrôleur dédié
- `controllers/ContractController.java` - Endpoints de fallback ajoutés
- `dtos/SignatureRequestDto.java` - DTO pour requêtes
- `dtos/SignatureResponseDto.java` - DTO pour réponses
- `pom.xml` - Dépendances iText ajoutées

### Frontend
- `components/ElectronicSignature.jsx` - Composant de signature
- `app/contracts/page.tsx` - Interface mise à jour
- `types/contracts.ts` - Types TypeScript mis à jour
- `services/api.js` - Fonctions API ajoutées

### Scripts et Documentation
- `add_signature_columns.sql` - Script de migration DB
- `test_signature_api.html` - Page de test API
- `TROUBLESHOOTING_SIGNATURE.md` - Guide de dépannage
- `start_contract_service.bat/.sh` - Scripts de démarrage

## 🎨 Interface Utilisateur

### Pour les Clients
- Bouton "✍️ Signer" visible si pas encore signé
- Canvas pour dessiner la signature
- Statut de signature affiché en temps réel
- PDF téléchargeable avec signatures

### Pour les Assureurs
- Même interface que les clients
- Peut signer après le client
- Voir le statut global du contrat

### Pour les Admins
- Voir tous les statuts de signature
- Télécharger tous les PDFs
- Gérer les contrats

## 🔒 Sécurité

- **Authentification JWT** requise pour tous les endpoints
- **Autorisation par rôle** : CLIENT, INSURER, ADMIN
- **Validation des signatures** : Format base64 vérifié
- **Audit trail** : Dates de signature enregistrées

## 📊 Base de Données

### Nouveaux champs dans `contrats`
- `client_signature` (TEXT) - Signature du client en base64
- `insurer_signature` (TEXT) - Signature de l'assureur en base64
- `client_signed_at` (DATE) - Date de signature du client
- `insurer_signed_at` (DATE) - Date de signature de l'assureur
- `is_fully_signed` (BOOLEAN) - Contrat entièrement signé

## 🚨 Points d'Attention

1. **Base de données** : Exécuter le script SQL avant de démarrer
2. **Dépendances** : iText peut prendre du temps à télécharger
3. **Logs** : Activer les logs de debug pour diagnostiquer
4. **Tokens** : S'assurer que l'authentification fonctionne
5. **CORS** : Configurer si nécessaire pour le frontend

## 🎉 Prochaines Étapes

1. **Tester** avec des données réelles
2. **Optimiser** les performances si nécessaire
3. **Ajouter** des validations supplémentaires
4. **Implémenter** la signature cryptographique si requis
5. **Ajouter** des tests unitaires automatisés
