package com.pfe.user.services;
import com.pfe.user.dtos.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUserById(String id);
    List<UserDto> getAllUsers();
    UserDto createUser(UserDto dto);
    UserDto updateUser(String id, UserDto dto);
    UserDto getOrCreateCurrentUser(String token);
    // ➕ AJOUT
    void deleteUser(String id); // ➕ AJOUT
}
