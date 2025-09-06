-- Migration pour ajouter les colonnes de signature électronique
ALTER TABLE contrats 
ADD COLUMN client_signature TEXT,
ADD COLUMN insurer_signature TEXT,
ADD COLUMN client_signed_at DATE,
ADD COLUMN insurer_signed_at DATE,
ADD COLUMN is_fully_signed BOOLEAN DEFAULT FALSE;
