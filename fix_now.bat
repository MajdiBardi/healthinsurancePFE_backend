@echo off
echo ===============================================
echo    CORRECTION URGENTE DE LA BASE DE DONNEES
echo ===============================================
echo.
echo Le probleme: Les colonnes de signature sont limitees a 255 caracteres
echo La solution: Les modifier en TYPE TEXT (illimite)
echo.
echo ===============================================
echo    ETAPES A SUIVRE:
echo ===============================================
echo.
echo 1. Ouvrir pgAdmin ou psql
echo 2. Se connecter a votre base de donnees PostgreSQL
echo 3. Executer ce script SQL:
echo.
echo    ALTER TABLE contrats ALTER COLUMN client_signature TYPE TEXT;
echo    ALTER TABLE contrats ALTER COLUMN insurer_signature TYPE TEXT;
echo.
echo 4. Redemarrer le service contract
echo 5. Tester la signature dans l'interface web
echo.
echo ===============================================
echo    FICHIERS DISPONIBLES:
echo ===============================================
echo - fix_database_now.sql (script SQL complet)
echo - check_database.sql (verification)
echo - SOLUTION_IMMEDIATE.md (guide detaille)
echo.
echo Appuyez sur une touche pour continuer...
pause > nul
