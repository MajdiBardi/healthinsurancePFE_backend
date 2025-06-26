package com.pfe.contract;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestPgsql {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/keycloak",
                    "postgres", "191jMT1019MB.."
            );


            System.out.println("✅ Connexion PostgreSQL réussie !");
            conn.close();
        } catch (Exception e) {
            System.out.println("❌ Échec de la connexion PostgreSQL :");
            e.printStackTrace();
        }
    }
}
