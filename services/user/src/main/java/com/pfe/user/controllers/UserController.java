package com.pfe.user.controllers;

import com.pfe.user.dtos.UserDto;
import com.pfe.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // autorise toutes les origines pour le frontend
public class UserController {

    private final UserService userService;

    /**
     * 🔐 Endpoint public pour initialiser automatiquement un utilisateur depuis le token Keycloak
     * Ce endpoint est appelé côté frontend au login pour créer l'utilisateur local s'il n'existe pas.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(userService.getOrCreateCurrentUser(token));
    }

    /**
     * ✅ Endpoint temporaire de debug des rôles (facultatif)
     */
    @GetMapping("/debug")
    public ResponseEntity<String> debugRoles(Authentication auth) {
        return ResponseEntity.ok("Roles: " + auth.getAuthorities().toString());
    }

    /**
     * ✅ Récupérer un utilisateur par ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * ✅ Lister tous les utilisateurs
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * ✅ Créer un nouvel utilisateur (manuel – rarement utilisé avec Keycloak)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> create(@RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    /**
     * ✅ Modifier un utilisateur par ID
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> update(@PathVariable String id, @RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    /**
     * ✅ Supprimer un utilisateur par ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
