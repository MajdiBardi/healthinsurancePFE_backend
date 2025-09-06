-- Script de correction IMMÉDIATE pour les colonnes de signature
-- Exécuter ce script dans votre base de données PostgreSQL

-- 1. Vérifier l'état actuel
SELECT 'AVANT CORRECTION:' as status;
SELECT column_name, data_type, character_maximum_length
FROM information_schema.columns 
WHERE table_name = 'contrats' 
AND column_name IN ('client_signature', 'insurer_signature');

-- 2. Corriger les colonnes de signature
DO $$
BEGIN
    -- Modifier client_signature si elle existe
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'contrats' AND column_name = 'client_signature') THEN
        ALTER TABLE contrats ALTER COLUMN client_signature TYPE TEXT;
        RAISE NOTICE 'Colonne client_signature modifiée en TEXT';
    ELSE
        ALTER TABLE contrats ADD COLUMN client_signature TEXT;
        RAISE NOTICE 'Colonne client_signature créée en TEXT';
    END IF;
    
    -- Modifier insurer_signature si elle existe
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'contrats' AND column_name = 'insurer_signature') THEN
        ALTER TABLE contrats ALTER COLUMN insurer_signature TYPE TEXT;
        RAISE NOTICE 'Colonne insurer_signature modifiée en TEXT';
    ELSE
        ALTER TABLE contrats ADD COLUMN insurer_signature TEXT;
        RAISE NOTICE 'Colonne insurer_signature créée en TEXT';
    END IF;
    
    -- Ajouter les autres colonnes si elles n'existent pas
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'contrats' AND column_name = 'client_signed_at') THEN
        ALTER TABLE contrats ADD COLUMN client_signed_at DATE;
        RAISE NOTICE 'Colonne client_signed_at créée';
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'contrats' AND column_name = 'insurer_signed_at') THEN
        ALTER TABLE contrats ADD COLUMN insurer_signed_at DATE;
        RAISE NOTICE 'Colonne insurer_signed_at créée';
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'contrats' AND column_name = 'is_fully_signed') THEN
        ALTER TABLE contrats ADD COLUMN is_fully_signed BOOLEAN DEFAULT FALSE;
        RAISE NOTICE 'Colonne is_fully_signed créée';
    END IF;
END $$;

-- 3. Vérifier le résultat
SELECT 'APRÈS CORRECTION:' as status;
SELECT column_name, data_type, character_maximum_length
FROM information_schema.columns 
WHERE table_name = 'contrats' 
AND column_name IN ('client_signature', 'insurer_signature', 'client_signed_at', 'insurer_signed_at', 'is_fully_signed')
ORDER BY column_name;
