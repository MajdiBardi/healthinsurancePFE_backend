@echo off
echo Correction de la base de données pour les signatures electroniques...
echo.
echo Veuillez executer ce script SQL dans votre base de données PostgreSQL:
echo.
echo ALTER TABLE contrats ALTER COLUMN client_signature TYPE TEXT;
echo ALTER TABLE contrats ALTER COLUMN insurer_signature TYPE TEXT;
echo.
echo Ou utilisez le fichier: fix_signature_columns.sql
echo.
pause
