package com.example.demo.dao.allusers;

import com.example.demo.models.dto.User;

import java.util.List;
import java.util.Map;

public interface AllUsersDao {
    User getUserByUsername(String username);

    boolean checkUsernameExists(String username);

    User addUser(String username, String password);

    boolean checkPassword(String username, String password);

    Map<String, String> getAll();
}
