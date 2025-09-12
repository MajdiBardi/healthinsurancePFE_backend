@echo off
echo ========================================
echo    Demarrage du Service ML
echo ========================================

cd /d "%~dp0\services\ml-service"

echo.
echo [1/3] Compilation du service ML...
call mvnw clean compile -q

if %ERRORLEVEL% neq 0 (
    echo ERREUR: Echec de la compilation
    pause
    exit /b 1
)

echo [2/3] Demarrage du service ML sur le port 8086...
echo.
echo Le service ML sera accessible sur: http://localhost:8086
echo APIs disponibles:
echo   - Prédiction: http://localhost:8086/api/ml/prediction/rachat-anticipe
echo   - Clustering: http://localhost:8086/api/ml/clustering/segment-client
echo   - Analytics: http://localhost:8086/api/ml/analytics/dashboard
echo.

call mvnw spring-boot:run

pause
