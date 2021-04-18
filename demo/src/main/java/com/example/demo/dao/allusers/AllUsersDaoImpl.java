package com.example.demo.dao.allusers;

import com.example.demo.models.dto.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AllUsersDaoImpl implements AllUsersDao {
    Map<String, User> usernameToUser = new HashMap<>();
    Map<User, String> userToUsername = new HashMap<>();
    Map<String, String> usernameToPassword = new HashMap<>();
    Map<String, User> tokenToUser = new HashMap<>();
    Map<User, String> userToToken = new HashMap<>();
    Map<Long, User> idToUser = new HashMap<>();

    private Long count = 0L;

    private Long generateUserId() {
        count++;
        return count;
    }

    @Override
    public User getUserByUsername(String username) {
        return usernameToUser.get(username);
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return usernameToUser.containsKey(username);
    }

    @Override
    public boolean checkTokenExists(String token) {
        return tokenToUser.containsKey(token);
    }

    @Override
    public boolean updateToken(String token, User user) {
        if (!userToToken.containsKey(user)) {
            return false;
        }
        var prevToken = userToToken.get(user);
        tokenToUser.remove(prevToken);
        tokenToUser.put(token, user);
        userToToken.replace(user, token);
        return true;
    }

    @Override
    public User getUserById(Long id) {
        return idToUser.get(id);
    }

    @Override
    public ArrayList<User> getAll() {
        return new ArrayList<>(idToUser.values());
    }

    @Override
    public String getTokenByUsername(String username) {
        var user = usernameToUser.get(username);
        if (user == null) {
            return null;
        }
        return userToToken.get(user);
    }

    @Override
    public User getUserByToken(String token) {
        return tokenToUser.get(token);
    }

    @Override
    public User updateUsernameById(Long id, String newUsername) {
        var prevUsername = userToUsername.get(idToUser.get(id));
        if (prevUsername == null) {
            return null;
        }
        var password = usernameToPassword.get(prevUsername);
        usernameToPassword.remove(prevUsername);
        usernameToPassword.put(newUsername, password);

        var user = usernameToUser.get(prevUsername);
        userToUsername.replace(user, newUsername);

        usernameToUser.remove(prevUsername);
        usernameToUser.put(newUsername, user);
        return user;
    }

    @Override
    public User updateUsernameByUsername(String prevUsername, String newUsername) {
        var id = usernameToUser.get(prevUsername).getId();
        return updateUsernameById(id, newUsername);
    }

    @Override
    public User updatePasswordById(Long id, String newPassword) {
        var user = idToUser.get(id);
        if (user == null) {
            return null;
        }
        var username = userToUsername.get(user);
        if (username == null) {
            return null;
        }
        usernameToPassword.replace(username, newPassword);
        return user;
    }

    @Override
    public User updatePasswordByUsername(String username, String newPassword) {
        var id = usernameToUser.get(username).getId();
        return updatePasswordById(id, newPassword);
    }

    @Override
    public User updatePointsById(Long id, Long newPoints) {
        var user = idToUser.get(id);
        if (user == null) {
            return null;
        }
        var newUser = new User(user);
        newUser.setPoints(newPoints);
        usernameToUser.replace(user.getUsername(), newUser);
        userToUsername.remove(user);
        userToUsername.put(newUser, user.getUsername());

        var token = userToToken.get(user);
        tokenToUser.replace(token, newUser);
        userToToken.remove(user);
        userToToken.put(newUser, token);

        idToUser.replace(id, newUser);
        return newUser;
    }

    @Override
    public User updatePointsByUsername(String username, Long newPoints) {
        var id = usernameToUser.get(username).getId();
        return updatePointsById(id, newPoints);
    }

    @Override
    public User addUser(String username, String password, String token) {
        if (checkUsernameExists(username)) {
            return null;
        }

        var user = new User(generateUserId(), username, password, 0L);
        usernameToUser.put(username, user);
        userToUsername.put(user, username);
        usernameToPassword.put(username, password);
        tokenToUser.put(token, user);
        userToToken.put(user, token);
        idToUser.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean checkPasswordIsCorrect(String username, String password) {
        return Objects.equals(usernameToPassword.get(username), password);
    }
}
