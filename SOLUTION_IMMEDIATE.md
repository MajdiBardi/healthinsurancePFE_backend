# 🚨 SOLUTION IMMÉDIATE - Erreur Signature

## ❌ Problème
```
ERROR: value too long for type character varying(255)
```

## ✅ SOLUTION RAPIDE

### Étape 1: Ouvrir pgAdmin ou psql
- Ouvrez **pgAdmin** ou **psql** 
- Connectez-vous à votre base de données PostgreSQL

### Étape 2: Exécuter ce script SQL
```sql
-- Corriger les colonnes de signature
ALTER TABLE contrats ALTER COLUMN client_signature TYPE TEXT;
ALTER TABLE contrats ALTER COLUMN insurer_signature TYPE TEXT;

-- Ajouter les autres colonnes si elles n'existent pas
ALTER TABLE contrats ADD COLUMN IF NOT EXISTS client_signed_at DATE;
ALTER TABLE contrats ADD COLUMN IF NOT EXISTS insurer_signed_at DATE;
ALTER TABLE contrats ADD COLUMN IF NOT EXISTS is_fully_signed BOOLEAN DEFAULT FALSE;
```

### Étape 3: Vérifier la correction
```sql
SELECT column_name, data_type, character_maximum_length
FROM information_schema.columns 
WHERE table_name = 'contrats' 
AND column_name IN ('client_signature', 'insurer_signature');
```

**Résultat attendu :**
- `client_signature` : `text` (pas de limite)
- `insurer_signature` : `text` (pas de limite)

### Étape 4: Redémarrer le service
```bash
cd services/contract
mvn spring-boot:run
```

### Étape 5: Tester
- Aller sur l'interface web
- Essayer de signer un contrat
- Ça devrait marcher ! 🎉

## 🔧 Alternative: Script automatique

Si vous avez accès à la ligne de commande PostgreSQL :

```bash
# Remplacer par vos paramètres de connexion
psql -h localhost -p 5432 -U postgres -d pfe_db -f fix_database_now.sql
```

## 📞 Si ça ne marche toujours pas

1. Vérifiez que vous êtes connecté à la bonne base de données
2. Vérifiez que la table s'appelle bien `contrats`
3. Exécutez le script de vérification : `check_database.sql`
