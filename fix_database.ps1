# Script PowerShell pour corriger la base de données
# Exécuter ce script pour corriger les colonnes de signature

Write-Host "🔧 Correction de la base de données pour les signatures électroniques..." -ForegroundColor Yellow

# Configuration de la base de données (modifiez selon votre configuration)
$DB_HOST = "localhost"
$DB_PORT = "5432"
$DB_NAME = "pfe_db"  # Remplacez par le nom de votre base de données
$DB_USER = "postgres"  # Remplacez par votre utilisateur
$DB_PASSWORD = "password"  # Remplacez par votre mot de passe

Write-Host "📋 Configuration de la base de données:" -ForegroundColor Cyan
Write-Host "   Host: $DB_HOST" -ForegroundColor Gray
Write-Host "   Port: $DB_PORT" -ForegroundColor Gray
Write-Host "   Database: $DB_NAME" -ForegroundColor Gray
Write-Host "   User: $DB_USER" -ForegroundColor Gray

# Vérifier si psql est disponible
try {
    $psqlVersion = psql --version
    Write-Host "✅ PostgreSQL client trouvé: $psqlVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ PostgreSQL client (psql) non trouvé. Veuillez l'installer ou utiliser pgAdmin." -ForegroundColor Red
    Write-Host "   Vous pouvez exécuter le script SQL manuellement dans pgAdmin ou psql." -ForegroundColor Yellow
    Write-Host "   Script à exécuter: fix_signature_columns.sql" -ForegroundColor Yellow
    exit 1
}

# Exécuter le script de correction
Write-Host "🚀 Exécution du script de correction..." -ForegroundColor Yellow

$env:PGPASSWORD = $DB_PASSWORD
$sqlFile = "fix_signature_columns.sql"

if (Test-Path $sqlFile) {
    try {
        psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f $sqlFile
        Write-Host "✅ Script de correction exécuté avec succès!" -ForegroundColor Green
    } catch {
        Write-Host "❌ Erreur lors de l'exécution du script: $_" -ForegroundColor Red
        Write-Host "   Exécutez manuellement le script SQL dans pgAdmin." -ForegroundColor Yellow
    }
} else {
    Write-Host "❌ Fichier $sqlFile non trouvé." -ForegroundColor Red
}

Write-Host "`n🎯 Prochaines étapes:" -ForegroundColor Cyan
Write-Host "1. Redémarrez le service contract" -ForegroundColor White
Write-Host "2. Testez la signature dans l'interface web" -ForegroundColor White
Write-Host "3. Vérifiez que les signatures sont sauvegardées" -ForegroundColor White

Write-Host "`n📝 Si vous préférez exécuter manuellement:" -ForegroundColor Yellow
Write-Host "   Ouvrez pgAdmin ou psql et exécutez: fix_signature_columns.sql" -ForegroundColor Gray
