package com.pfe.user.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class JwtUtils {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String extractUserId(String token) {
        return extractSimpleClaim(token, "sub");
    }

    public String extractEmail(String token) {
        return extractSimpleClaim(token, "email");
    }

    public String extractName(String token) {
        return extractSimpleClaim(token, "name");
    }

    public String extractRole(String token) {
        try {
            JsonNode payload = extractPayload(token);
            JsonNode roles = payload
                    .path("realm_access")
                    .path("roles");

            if (roles.isArray()) {
                for (JsonNode roleNode : roles) {
                    String role = roleNode.asText().toUpperCase();
                    if (role.matches("ADMIN|CLIENT|INSURER|BENEFICIARY")) {
                        return role;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String extractSimpleClaim(String token, String key) {
        try {
            return extractPayload(token).path(key).asText(null);
        } catch (Exception e) {
            return null;
        }
    }

    private JsonNode extractPayload(String token) throws Exception {
        String[] parts = token.replace("Bearer ", "").split("\\.");
        String json = new String(Base64.getUrlDecoder().decode(parts[1]));
        return objectMapper.readTree(json);
    }
}
