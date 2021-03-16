package com.example.demo.service.user;

import com.example.demo.models.dto.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    User getUserById(Long id);

    User getRandomUser();

    User addUser(User newUser);

    List<User> getAll();

    User updateUser(Long id, User user);

    void deleteUserById(Long id);

    // returns all token-user information
    Map<String, User> getUsersTokens();

    // returns all login-password information
    Map<String, String> getAllUsersInfo();
}
