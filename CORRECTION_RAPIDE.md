# 🚨 Correction Rapide - Erreur Signature Trop Longue

## Problème Identifié
```
ERROR: value too long for type character varying(255)
```

Les colonnes de signature dans la base de données sont limitées à 255 caractères, mais les signatures base64 peuvent être beaucoup plus longues.

## 🔧 Solution Rapide

### Option 1: Script Automatique (Recommandé)
```powershell
# Exécuter dans PowerShell
.\fix_database.ps1
```

### Option 2: Correction Manuelle
1. **Ouvrir pgAdmin** ou **psql**
2. **Exécuter ce script SQL** :
```sql
-- Modifier les colonnes existantes
ALTER TABLE contrats ALTER COLUMN client_signature TYPE TEXT;
ALTER TABLE contrats ALTER COLUMN insurer_signature TYPE TEXT;
```

### Option 3: Supprimer et Recréer
```sql
-- Supprimer les colonnes
ALTER TABLE contrats DROP COLUMN IF EXISTS client_signature;
ALTER TABLE contrats DROP COLUMN IF EXISTS insurer_signature;
ALTER TABLE contrats DROP COLUMN IF EXISTS client_signed_at;
ALTER TABLE contrats DROP COLUMN IF EXISTS insurer_signed_at;
ALTER TABLE contrats DROP COLUMN IF EXISTS is_fully_signed;

-- Recréer avec la bonne taille
ALTER TABLE contrats 
ADD COLUMN client_signature TEXT,
ADD COLUMN insurer_signature TEXT,
ADD COLUMN client_signed_at DATE,
ADD COLUMN insurer_signed_at DATE,
ADD COLUMN is_fully_signed BOOLEAN DEFAULT FALSE;
```

## ✅ Vérification

Après la correction, vérifiez que les colonnes ont la bonne taille :
```sql
SELECT column_name, data_type, character_maximum_length 
FROM information_schema.columns 
WHERE table_name = 'contrats' 
AND column_name IN ('client_signature', 'insurer_signature');
```

**Résultat attendu :**
- `client_signature` : `text` (pas de limite de longueur)
- `insurer_signature` : `text` (pas de limite de longueur)

## 🚀 Test

1. **Redémarrer le service contract**
2. **Aller sur l'interface web**
3. **Essayer de signer un contrat**
4. **Vérifier que ça fonctionne !**

## 📞 Si ça ne marche toujours pas

1. **Vérifier les logs** du service contract
2. **Vérifier la connexion** à la base de données
3. **Exécuter le script de test** : `test_signature_api.html`
