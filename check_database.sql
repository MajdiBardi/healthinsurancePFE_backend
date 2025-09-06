-- Vérifier l'état actuel des colonnes de signature
SELECT 
    column_name, 
    data_type, 
    character_maximum_length,
    is_nullable
FROM information_schema.columns 
WHERE table_name = 'contrats' 
AND column_name IN ('client_signature', 'insurer_signature', 'client_signed_at', 'insurer_signed_at', 'is_fully_signed')
ORDER BY column_name;
