# 🔍 DIAGNOSTIC RÔLES - Problème de Signature

## ❌ Problème identifié
L'assureur signe mais c'est traité comme si c'était le client qui signait.

## 🔍 Causes possibles

### 1. **Problème de détection du rôle**
- L'authentification ne retourne pas le bon rôle
- La logique de détermination du rôle est incorrecte
- Les rôles ne sont pas correctement configurés dans Keycloak

### 2. **Problème de cache**
- L'application utilise des données en cache
- Les rôles ne sont pas mis à jour

### 3. **Problème de configuration**
- Les rôles ne sont pas correctement mappés
- L'authentification ne fonctionne pas comme attendu

## ✅ Corrections apportées

### 1. **Amélioration de la détection du rôle**
```java
// Ancien code (incorrect)
String userRole = authentication.getAuthorities().stream()
    .anyMatch(auth -> auth.getAuthority().equals("ROLE_CLIENT")) ? "CLIENT" : "INSURER";

// Nouveau code (correct)
String userRole = "UNKNOWN";
if (authentication.getAuthorities().stream()
        .anyMatch(auth -> auth.getAuthority().equals("ROLE_CLIENT"))) {
    userRole = "CLIENT";
} else if (authentication.getAuthorities().stream()
        .anyMatch(auth -> auth.getAuthority().equals("ROLE_INSURER"))) {
    userRole = "INSURER";
} else if (authentication.getAuthorities().stream()
        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
    userRole = "INSURER"; // Admin peut signer comme assureur
}
```

### 2. **Ajout de logs détaillés**
- Logs pour voir les rôles détectés
- Logs pour voir l'ID du signataire
- Logs pour voir le rôle reçu dans le service

## 🧪 Tests à effectuer

### 1. **Test des rôles**
Ouvrez `test_roles.html` et :
1. Vérifiez vos rôles utilisateur
2. Testez la signature comme CLIENT
3. Testez la signature comme INSURER
4. Vérifiez les signatures sauvegardées

### 2. **Vérification des logs**
Regardez les logs du service contract pour voir :
- `=== CONTRACT CONTROLLER SIGN ===`
- `Rôle détecté: [ROLE]`
- `🔍 Rôle reçu: [ROLE]`
- `✅ Signature [ROLE] appliquée`

### 3. **Test en base de données**
```sql
-- Vérifier les signatures sauvegardées
SELECT 
    id,
    client_signature IS NOT NULL as client_signed,
    insurer_signature IS NOT NULL as insurer_signed,
    is_fully_signed
FROM contrats 
WHERE id = [VOTRE_CONTRAT_ID];
```

## 🚀 Solutions

### Solution 1: Redémarrage complet
1. Arrêter le service contract
2. Redémarrer le service contract
3. Tester avec `test_roles.html`

### Solution 2: Vérification des rôles Keycloak
1. Vérifier que l'utilisateur a le bon rôle
2. Vérifier que le rôle est correctement mappé
3. Tester la connexion

### Solution 3: Test manuel
1. Connectez-vous comme CLIENT
2. Signez un contrat
3. Connectez-vous comme INSURER
4. Signez le même contrat
5. Vérifiez que les deux signatures sont différentes

## 📊 Résultats attendus

Après les corrections :
- ✅ CLIENT signe → `client_signature` mise à jour
- ✅ INSURER signe → `insurer_signature` mise à jour
- ✅ Les deux signent → `is_fully_signed` = true
- ✅ Les logs montrent le bon rôle

## 🆘 Si le problème persiste

1. **Vérifiez les rôles dans Keycloak**
2. **Vérifiez la configuration Spring Security**
3. **Regardez les logs détaillés**
4. **Testez avec différents utilisateurs**
