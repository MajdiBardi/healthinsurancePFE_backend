-- Script de correction rapide pour les colonnes de signature
-- Exécuter ce script dans votre base de données PostgreSQL

-- Vérifier d'abord la structure actuelle
SELECT column_name, data_type, character_maximum_length 
FROM information_schema.columns 
WHERE table_name = 'contrats' 
AND column_name IN ('client_signature', 'insurer_signature', 'client_signed_at', 'insurer_signed_at', 'is_fully_signed');

-- Si les colonnes existent avec VARCHAR(255), les modifier
DO $$
BEGIN
    -- Modifier client_signature si elle existe et est VARCHAR(255)
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'contrats' 
        AND column_name = 'client_signature' 
        AND data_type = 'character varying' 
        AND character_maximum_length = 255
    ) THEN
        ALTER TABLE contrats ALTER COLUMN client_signature TYPE TEXT;
        RAISE NOTICE 'Colonne client_signature modifiée en TEXT';
    END IF;
    
    -- Modifier insurer_signature si elle existe et est VARCHAR(255)
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'contrats' 
        AND column_name = 'insurer_signature' 
        AND data_type = 'character varying' 
        AND character_maximum_length = 255
    ) THEN
        ALTER TABLE contrats ALTER COLUMN insurer_signature TYPE TEXT;
        RAISE NOTICE 'Colonne insurer_signature modifiée en TEXT';
    END IF;
END $$;

-- Vérifier le résultat final
SELECT column_name, data_type, character_maximum_length 
FROM information_schema.columns 
WHERE table_name = 'contrats' 
AND column_name IN ('client_signature', 'insurer_signature', 'client_signed_at', 'insurer_signed_at', 'is_fully_signed');
