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
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(userService.getOrCreateCurrentUser(token));
    }

    @GetMapping("/debug")
    public ResponseEntity<String> debugRoles(Authentication auth) {
        return ResponseEntity.ok("Roles: " + auth.getAuthorities().toString());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> create(@RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> update(@PathVariable String id, @RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Nouveaux endpoints spécifiques pour filtrer par rôle
    @GetMapping("/clients")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getClients() {
        return ResponseEntity.ok(userService.getUsersByRole("CLIENT"));
    }

    @GetMapping("/insurers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getInsurers() {
        return ResponseEntity.ok(userService.getUsersByRole("INSURER"));
    }

    @GetMapping("/beneficiaries")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getBeneficiaries() {
        return ResponseEntity.ok(userService.getUsersByRole("BENEFICIARY"));
    }
}
