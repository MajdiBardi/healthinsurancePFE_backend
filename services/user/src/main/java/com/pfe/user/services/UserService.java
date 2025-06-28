package com.pfe.user.services;

import com.pfe.user.dtos.UserDto;

import java.util.List;

public interface UserService {

    UserDto getOrCreateCurrentUser(String token);

    UserDto getUserById(String id);

    List<UserDto> getAllUsers();

    UserDto createUser(UserDto dto);

    UserDto updateUser(String id, UserDto dto);

    void deleteUser(String id);

    List<UserDto> getUsersByRole(String role); // ✅ Nouveau
}
