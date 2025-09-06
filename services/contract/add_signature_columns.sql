-- Script SQL pour ajouter les colonnes de signature électronique
-- Exécuter ce script dans votre base de données PostgreSQL

-- D'abord, supprimer les colonnes si elles existent avec la mauvaise taille
ALTER TABLE contrats DROP COLUMN IF EXISTS client_signature;
ALTER TABLE contrats DROP COLUMN IF EXISTS insurer_signature;
ALTER TABLE contrats DROP COLUMN IF EXISTS client_signed_at;
ALTER TABLE contrats DROP COLUMN IF EXISTS insurer_signed_at;
ALTER TABLE contrats DROP COLUMN IF EXISTS is_fully_signed;

-- Ajouter les colonnes avec la bonne taille (TEXT pour les signatures)
ALTER TABLE contrats 
ADD COLUMN client_signature TEXT,
ADD COLUMN insurer_signature TEXT,
ADD COLUMN client_signed_at DATE,
ADD COLUMN insurer_signed_at DATE,
ADD COLUMN is_fully_signed BOOLEAN DEFAULT FALSE;

-- Vérifier que les colonnes ont été ajoutées
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'contrats' 
AND column_name IN ('client_signature', 'insurer_signature', 'client_signed_at', 'insurer_signed_at', 'is_fully_signed');
