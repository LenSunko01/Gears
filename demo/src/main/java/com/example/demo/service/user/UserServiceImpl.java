package com.example.demo.service.user;

import com.example.demo.dao.allusers.AllUsersDao;
import com.example.demo.models.dto.User;
import com.example.demo.web.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    private final AllUsersDao allUsers;

    public UserServiceImpl(AllUsersDao allUsers) {
        this.allUsers = allUsers;
    }

    @Override
    public User getUserById(Long id) {
        return allUsers.getUserById(id);
    }

    @Override
    public User getRandomUser() {
        List<User> list = allUsers.getAll();
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    @Override
    public List<User> getAll() {
        return allUsers.getAll();
    }

    @Override
    public User updateUsername(String username, String newUsername) {
        var id = allUsers.getUserByUsername(username).getId();
        return allUsers.updateUsernameById(id, newUsername);
    }

    @Override
    public User updatePassword(String username, String newPassword) {
        var id = allUsers.getUserByUsername(username).getId();
        return allUsers.updatePasswordById(id, newPassword);
    }

    @Override
    public User updatePoints(String username, Long newPoints) {
        var id = allUsers.getUserByUsername(username).getId();
        return allUsers.updatePointsById(id, newPoints);
    }

    @Override
    public void deleteUser(String username) {
        allUsers.deleteUser(username);
    }

}
