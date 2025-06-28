package com.pfe.user.services;

import com.pfe.user.dtos.UserDto;
import com.pfe.user.entities.User;
import com.pfe.user.repositories.UserRepository;
import com.pfe.user.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Override
    public UserDto getOrCreateCurrentUser(String token) {
        String userId = jwtUtils.extractUserId(token);
        return userRepository.findById(userId)
                .map(this::mapToDto)
                .orElseGet(() -> {
                    User user = new User();
                    user.setId(userId);
                    user.setName(jwtUtils.extractName(token));
                    user.setEmail(jwtUtils.extractEmail(token));
                    user.setRole(jwtUtils.extractRole(token));
                    return mapToDto(userRepository.save(user));
                });
    }

    @Override
    public UserDto getUserById(String id) {
        return userRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToDto).toList();
    }

    @Override
    public UserDto createUser(UserDto dto) {
        User user = mapToEntity(dto);
        return mapToDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(String id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        return mapToDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    // ✅ Ajout : récupération des utilisateurs par rôle
    @Override
    public List<UserDto> getUsersByRole(String role) {
        return userRepository.findByRoleIgnoreCase(role)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    // --- Mapping utils ---
    private UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }

    private User mapToEntity(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        return user;
    }
}
