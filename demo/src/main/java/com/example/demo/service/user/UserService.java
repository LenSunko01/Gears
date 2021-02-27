package com.example.demo.service.user;

import com.example.demo.models.dto.User;

import java.util.List;

public interface UserService {
    User getUserById(Long id);

    User getRandomUser();

    User addUser(User newUser);

    List<User> getAll();

    User replaceUser(Long id, User user);

    void deleteUserById(Long id);
}
