package com.pfe.user.entities;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id; // This will be the Keycloak ID (UUID)

    private String name;
    private String email;
    private String role; // "CLIENT", "ADMIN", "INSURER"...
}