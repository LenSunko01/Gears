//package com.example.demo.dao.allusers;
//
//import com.example.demo.models.dto.User;
//import org.springframework.stereotype.Repository;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Repository
//public class AllUsersDaoImpl implements AllUsersDao {
//    Map<String, User> loginStorage = new HashMap<>();
//    Map<String, String> passwordStorage = new HashMap<>();
//    Map<String, User> tokenStorage = new HashMap<>();
//    Map<Long, User> userStorage = new HashMap<>();
//
//    @Override
//    public User getUserByUsername(String username) {
//        return loginStorage.get(username);
//    }
//
//    @Override
//    public boolean checkUsernameExists(String username) {
//        return loginStorage.containsKey(username);
//    }
//
//    @Override
//    public boolean checkTokenExists(String token) {
//        return tokenStorage.containsKey(token);
//    }
//
//    @Override
//    public boolean updateToken(String token, User user) {
//        if (!tokenStorage.containsValue(user)) {
//            return false;
//        }
//        tokenStorage.entrySet().removeIf(e -> e.getValue().equals(user));
//        tokenStorage.put(token, user);
//        return true;
//    }
//
//    @Override
//    public User getUserById(Long id) {
//        return userStorage.get(id);
//    }
//
//    @Override
//    public List<User> getAll() {
//        return new ArrayList<>(userStorage.values());
//    }
//
//    @Override
//    public String getTokenByUsername(String username) {
//        for (Map.Entry<String, User> entry : tokenStorage.entrySet()) {
//            if (entry.getValue().getUsername().equals(username)) {
//                return entry.getKey();
//            }
//        }
//    }
//
//    @Override
//    public User getUserByToken(String token) {
//        return tokenStorage.get(token);
//    }
//
//    @Override
//    public User updateUsernameById(Long id, String newUsername) {
//        return null;
//    }
//
//    @Override
//    public User updatePasswordById(Long id, String newPassword) {
//        return null;
//    }
//
//    @Override
//    public User updatePointsById(Long id, Long newPoints) {
//        return null;
//    }
//
//    @Override
//    public boolean deleteUser(String username) {
//        return false;
//    }
//
//    private Long generateUserId() {
//        long id = new Random().nextLong();
//        while (userStorage.containsKey(id)) {
//            id = new Random().nextLong();
//        }
//        return id;
//    }
//
//    @Override
//    public User addUser(String username, String password, String token) {
//        if (checkUsernameExists(username)) {
//            return null;
//        }
//
//        if (checkTokenExists(token)) {
//            return null;
//        }
//
//        var user = new User(generateUserId(), password, token, 0L);
//        loginStorage.put(username, user);
//        passwordStorage.put(username, password);
//        tokenStorage.put(token, user);
//        userStorage.put(user.getId(), user);
//
//        return user;
//    }
//
//    @Override
//    public boolean checkPassword(String username, String password) {
//        return Objects.equals(passwordStorage.get(username), password);
//    }
//}
