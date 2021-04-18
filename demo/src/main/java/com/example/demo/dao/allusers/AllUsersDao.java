package com.example.demo.dao.allusers;

import com.example.demo.models.dto.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface AllUsersDao {
    User getUserByUsername(String username);

    boolean checkUsernameExists(String username);

    User addUser(String username, String password, String token);

    boolean checkPasswordIsCorrect(String username, String password);

    boolean checkTokenExists(String token);

    User getUserById(Long id);

    User getUserByToken(String token);

    ArrayList<User> getAll();

    String getTokenByUsername(String username);

    User updateUsernameById(Long id, String newUsername);

    User updateUsernameByUsername(String prevUsername, String newUsername);

    User updatePasswordById(Long id, String newPassword);

    User updatePasswordByUsername(String username, String newPassword);

    User updatePointsById(Long id, Long newPoints);

    User updatePointsByUsername(String username, Long newPoints);

    boolean updateToken(String token, User user);
}
