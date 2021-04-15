package com.example.demo.service.user;

import com.example.demo.models.dto.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    User getUserById(Long id);

    User getRandomUser();

    List<User> getAll();

    User updateUsername(String username, String newUsername);

    User updatePassword(String username, String newPassword);

    User updatePoints(String username, Long newPoints);

    void deleteUser(String username);
}
