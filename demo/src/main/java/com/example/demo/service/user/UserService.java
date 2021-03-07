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

    Map<String, User> getActiveUsers();

    Map<String, String> getAllUsersInfo();
}
