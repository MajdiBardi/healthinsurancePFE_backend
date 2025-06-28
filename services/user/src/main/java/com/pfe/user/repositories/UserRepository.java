package com.pfe.user.repositories;

import com.pfe.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByRoleIgnoreCase(String role); // ✅ Nouveau
    List<User> findByRole(String role); //
}
