package com.example.demo.dao.allusers;

import com.example.demo.models.dto.User;

import java.util.List;
import java.util.Map;

public interface AllUsersDao {
    User getUserByUsername(String username);

    boolean checkUsernameExists(String username);

    User addUser(String username, String password, String token);

    boolean checkPassword(String username, String password);

    boolean checkTokenExists(String token);

    boolean updateToken(String token, User user);

    User getUserById(Long id);

    List<User> getAll();

    String getTokenByUsername(String username);

    User getUserByToken(String token);

    User updateUsernameById(Long id, String newUsername);

    User updatePasswordById(Long id, String newPassword);

    User updatePointsById(Long id, Long newPoints);

    boolean deleteUser(String username);
}
