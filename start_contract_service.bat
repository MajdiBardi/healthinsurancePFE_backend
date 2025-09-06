@echo off
echo Démarrage du service Contract avec signature électronique...

echo.
echo 1. Vérification de la base de données...
echo Exécutez d'abord le script SQL: add_signature_columns.sql

echo.
echo 2. Compilation du service...
cd services\contract
call mvn clean compile

echo.
echo 3. Démarrage du service...
call mvn spring-boot:run

pause
