package com.example.demo.service.user;

import com.example.demo.models.dto.User;

import java.util.ArrayList;

public interface UserService {
    User getUserById(Long id);

    User getRandomUser();

    ArrayList<User> getAll();

    User updateUsername(Long id, String newUsername);

    User updatePassword(Long id, String newPassword);

    User updatePoints(Long id, Long newPoints);

    User updateUsername(String username, String newUsername);

    User updatePassword(String password, String newPassword);

    User updatePoints(String username, Long newPoints);
}
