-- Script de débogage pour vérifier l'état de la base de données
-- Exécuter ce script pour diagnostiquer le problème

-- 1. Vérifier la structure de la table
SELECT 'STRUCTURE DE LA TABLE:' as info;
SELECT column_name, data_type, character_maximum_length, is_nullable
FROM information_schema.columns 
WHERE table_name = 'contrats' 
ORDER BY ordinal_position;

-- 2. Vérifier spécifiquement les colonnes de signature
SELECT 'COLONNES DE SIGNATURE:' as info;
SELECT column_name, data_type, character_maximum_length
FROM information_schema.columns 
WHERE table_name = 'contrats' 
AND column_name IN ('client_signature', 'insurer_signature', 'client_signed_at', 'insurer_signed_at', 'is_fully_signed');

-- 3. Vérifier s'il y a des données dans la table
SELECT 'DONNÉES EXISTANTES:' as info;
SELECT COUNT(*) as nombre_contrats FROM contrats;

-- 4. Vérifier les contraintes de la table
SELECT 'CONTRAINTES:' as info;
SELECT constraint_name, constraint_type 
FROM information_schema.table_constraints 
WHERE table_name = 'contrats';

-- 5. Tester l'insertion d'une signature de test
SELECT 'TEST D''INSERTION:' as info;
-- Cette requête va échouer si les colonnes n'existent pas ou ont la mauvaise taille
SELECT 
    id,
    client_signature,
    insurer_signature,
    is_fully_signed
FROM contrats 
LIMIT 1;
