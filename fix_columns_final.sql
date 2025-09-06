-- Script final pour corriger les colonnes de signature
-- Exécuter ce script dans pgAdmin

-- 1. Vérifier l'état actuel
SELECT 'AVANT CORRECTION:' as status;
SELECT column_name, data_type, character_maximum_length
FROM information_schema.columns 
WHERE table_name = 'contrats' 
AND column_name IN ('client_signature', 'insurer_signature');

-- 2. Modifier les colonnes en TEXT
ALTER TABLE contrats ALTER COLUMN client_signature TYPE TEXT;
ALTER TABLE contrats ALTER COLUMN insurer_signature TYPE TEXT;

-- 3. Vérifier le résultat
SELECT 'APRÈS CORRECTION:' as status;
SELECT column_name, data_type, character_maximum_length
FROM information_schema.columns 
WHERE table_name = 'contrats' 
AND column_name IN ('client_signature', 'insurer_signature');

-- 4. Tester avec une signature de test
UPDATE contrats 
SET client_signature = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg=='
WHERE id = 46;

-- 5. Vérifier que la signature a été sauvegardée
SELECT id, client_signature, LENGTH(client_signature) as signature_length
FROM contrats 
WHERE id = 46;
