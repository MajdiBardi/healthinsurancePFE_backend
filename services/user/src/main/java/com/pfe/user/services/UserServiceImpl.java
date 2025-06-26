package com.pfe.user.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.pfe.user.dtos.UserDto;
import com.pfe.user.entities.User;
import com.pfe.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto getUserById(String id) {
        return userRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto dto) {
        User user = User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .role(dto.getRole())
                .build();
        return toDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(String id, UserDto dto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setRole(dto.getRole());

        return toDto(userRepository.save(existing));
    }

    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    // 🔁 Nouvelle méthode pour init via token
    public UserDto getOrCreateCurrentUser(String token) {
        String username = extractUsername(token);
        Optional<User> existing = userRepository.findById(username); // l’id est username

        if (existing.isPresent()) return toDto(existing.get());

        // Sinon : créer
        User newUser = User.builder()
                .id(username)
                .name(username) // Ou extraire le nom complet si disponible
                .email(extractEmail(token))
                .role(extractRole(token))
                .build();

        return toDto(userRepository.save(newUser));
    }

    private String extractUsername(String token) {
        DecodedJWT jwt = JWT.decode(token.replace("Bearer ", ""));
        return jwt.getClaim("preferred_username").asString();
    }

    private String extractEmail(String token) {
        DecodedJWT jwt = JWT.decode(token.replace("Bearer ", ""));
        return jwt.getClaim("email").asString();
    }

    private String extractRole(String token) {
        DecodedJWT jwt = JWT.decode(token.replace("Bearer ", ""));
        List<String> roles = (List<String>) jwt.getClaim("realm_access").asMap().get("roles");

        if (roles.contains("ADMIN")) return "ADMIN";
        if (roles.contains("CLIENT")) return "CLIENT";
        if (roles.contains("INSURER")) return "INSURER";
        if (roles.contains("BENEFICIARY")) return "BENEFICIARY";

        return "USER";
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
