package com.pfe.user.utils;



import org.springframework.context.annotation.Configuration;

import java.util.Base64;
@Configuration

public class JwtUtils {

    public static String extractUserId(String token) {
        return extractClaim(token, "sub"); // ou "userId" selon ton JWT
    }

    public static String extractEmail(String token) {
        return extractClaim(token, "email");
    }

    public static String extractName(String token) {
        return extractClaim(token, "name");
    }

    public static String extractRole(String token) {
        return extractClaim(token, "role");
    }

    private static String extractClaim(String token, String claimKey) {
        try {
            String[] parts = token.replace("Bearer ", "").split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            // Extrait la valeur du champ claimKey dans le JSON (très basique)
            int start = payload.indexOf("\"" + claimKey + "\":");
            if (start == -1) return null;
            int startQuote = payload.indexOf("\"", start + claimKey.length() + 3);
            int endQuote = payload.indexOf("\"", startQuote + 1);
            return payload.substring(startQuote + 1, endQuote);
        } catch (Exception e) {
            return null;
        }
    }
}
