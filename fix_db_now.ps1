# Script PowerShell pour corriger IMMÉDIATEMENT la base de données
Write-Host "🚨 CORRECTION URGENTE DE LA BASE DE DONNÉES" -ForegroundColor Red
Write-Host "===============================================" -ForegroundColor Red

# Configuration de la base de données
$DB_HOST = "localhost"
$DB_PORT = "5432"
$DB_NAME = "pfe_db"  # MODIFIEZ SELON VOTRE CONFIGURATION
$DB_USER = "postgres"  # MODIFIEZ SELON VOTRE CONFIGURATION
$DB_PASSWORD = "password"  # MODIFIEZ SELON VOTRE CONFIGURATION

Write-Host "📋 Configuration:" -ForegroundColor Yellow
Write-Host "   Host: $DB_HOST" -ForegroundColor Gray
Write-Host "   Port: $DB_PORT" -ForegroundColor Gray
Write-Host "   Database: $DB_NAME" -ForegroundColor Gray
Write-Host "   User: $DB_USER" -ForegroundColor Gray

# Demander confirmation
$confirmation = Read-Host "Voulez-vous continuer? (y/N)"
if ($confirmation -ne 'y' -and $confirmation -ne 'Y') {
    Write-Host "❌ Annulé par l'utilisateur" -ForegroundColor Red
    exit
}

# Vérifier si psql est disponible
try {
    $psqlVersion = psql --version 2>$null
    Write-Host "✅ PostgreSQL client trouvé" -ForegroundColor Green
} catch {
    Write-Host "❌ PostgreSQL client (psql) non trouvé!" -ForegroundColor Red
    Write-Host "   Veuillez installer PostgreSQL ou utiliser pgAdmin" -ForegroundColor Yellow
    Write-Host "   Script à exécuter manuellement: fix_database_now.sql" -ForegroundColor Yellow
    exit 1
}

# Exécuter le script de correction
Write-Host "🔧 Exécution de la correction..." -ForegroundColor Yellow

$env:PGPASSWORD = $DB_PASSWORD

try {
    psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f "fix_database_now.sql"
    Write-Host "✅ Correction exécutée avec succès!" -ForegroundColor Green
    Write-Host "🚀 Vous pouvez maintenant redémarrer le service et tester la signature!" -ForegroundColor Green
} catch {
    Write-Host "❌ Erreur lors de l'exécution: $_" -ForegroundColor Red
    Write-Host "   Exécutez manuellement le script: fix_database_now.sql" -ForegroundColor Yellow
}

Write-Host "`n📝 Prochaines étapes:" -ForegroundColor Cyan
Write-Host "1. Redémarrez le service contract (mvn spring-boot:run)" -ForegroundColor White
Write-Host "2. Testez la signature dans l'interface web" -ForegroundColor White
Write-Host "3. Les signatures devraient maintenant fonctionner!" -ForegroundColor White
