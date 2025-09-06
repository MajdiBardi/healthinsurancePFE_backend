# Guide de Dépannage - Signature Électronique

## Problème : Erreur 400 lors de la signature

### Étapes de diagnostic :

#### 1. Vérifier la base de données
```sql
-- Exécuter ce script dans PostgreSQL
ALTER TABLE contrats 
ADD COLUMN IF NOT EXISTS client_signature TEXT,
ADD COLUMN IF NOT EXISTS insurer_signature TEXT,
ADD COLUMN IF NOT EXISTS client_signed_at DATE,
ADD COLUMN IF NOT EXISTS insurer_signed_at DATE,
ADD COLUMN IF NOT EXISTS is_fully_signed BOOLEAN DEFAULT FALSE;
```

#### 2. Vérifier les logs du backend
- Redémarrer le service contract
- Regarder les logs pour voir les messages de debug ajoutés
- Chercher les messages "=== CONTRACT CONTROLLER SIGN ===" et "=== SIGNATURE REQUEST ==="

#### 3. Tester l'API directement
- Ouvrir `test_signature_api.html` dans le navigateur
- Tester les endpoints un par un
- Vérifier que le token d'authentification est valide

#### 4. Vérifier les endpoints disponibles
```bash
# Test de l'endpoint de statut
curl -H "Authorization: Bearer YOUR_TOKEN" \
     http://localhost:8222/api/contracts/1/signature-status

# Test de l'endpoint de test
curl -H "Authorization: Bearer YOUR_TOKEN" \
     http://localhost:8222/api/contracts/1/test-signature
```

#### 5. Vérifier la configuration Spring Security
- S'assurer que les endpoints `/api/contracts/*` sont autorisés
- Vérifier que l'authentification fonctionne correctement

### Solutions possibles :

#### Solution 1 : Problème de base de données
Si les colonnes de signature n'existent pas :
```sql
-- Exécuter le script SQL fourni
-- Redémarrer l'application
```

#### Solution 2 : Problème d'authentification
- Vérifier que le token JWT est valide
- Vérifier que l'utilisateur a le bon rôle (CLIENT ou INSURER)

#### Solution 3 : Problème de validation
- Les logs montreront si la signature est rejetée
- Vérifier que la signature base64 est valide

#### Solution 4 : Problème de configuration
- Vérifier que tous les services sont démarrés
- Vérifier que les dépendances sont correctement injectées

### Endpoints de test disponibles :

1. `GET /api/contracts/{id}/signature-status` - Statut de signature
2. `GET /api/contracts/{id}/test-signature` - Test simple
3. `POST /api/contracts/{id}/sign` - Signature du contrat

### Logs à surveiller :

```
=== CONTRACT CONTROLLER SIGN ===
Contract ID from path: [ID]
Request body: [SignatureRequestDto]
Authentication name: [USER_ID]
Authentication authorities: [ROLES]

=== SIGNATURE REQUEST ===
Contract ID: [ID]
Signer ID: [USER_ID]
Signer Role: [ROLE]
Signature length: [LENGTH]
Signature preview: [PREVIEW]
```

### Messages d'erreur courants :

- **"Contract not found"** : L'ID du contrat n'existe pas
- **"Signature cannot be empty"** : La signature est vide
- **"Invalid signature format"** : La signature n'est pas en base64 valide
- **"Invalid signer role"** : Le rôle de l'utilisateur n'est pas valide
