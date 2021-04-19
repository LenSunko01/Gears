package com.example.demo.service.user;

import com.example.demo.dao.allusers.AllUsersDao;
import com.example.demo.models.dto.User;
import com.example.demo.service.registration.RegistrationService;
import com.example.demo.web.exceptions.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final AllUsersDao allUsers;
    private final RegistrationService registrationService;

    public UserServiceImpl(AllUsersDao allUsers, RegistrationService registrationService) {
        this.allUsers = allUsers;
        this.registrationService = registrationService;
    }

    private void checkTokenIsCorrect(Long id, String token) {
        var user = allUsers.getUserById(id);
        var username = user.getUsername();
        var correctToken = allUsers.getTokenByUsername(username);
        if (!correctToken.equals(token)) {
            throw new AuthenticationException();
        }
    }

    @Override
    public User getUserById(Long id, String token) {
        checkTokenIsCorrect(id, token);
        return allUsers.getUserById(id);
    }

    @Override
    public Map.Entry<String, Long> getRandomUser() {
        Map<String, Long> map = allUsers.getAll();
        var list = new ArrayList<>(map.entrySet());
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    @Override
    public Map<String, Long> getAll() {
        return allUsers.getAll();
    }

    @Override
    public User updateUsername(Long id, String newUsername, String token) {
        checkTokenIsCorrect(id, token);
        registrationService.checkLoginIsValid(newUsername);
        return allUsers.updateUsernameById(id, newUsername);
    }

    @Override
    public User updatePassword(Long id, String newPassword, String token) {
        checkTokenIsCorrect(id, token);
        registrationService.checkPasswordIsValid(newPassword);
        return allUsers.updatePasswordById(id, newPassword);
    }

    @Override
    public User updatePoints(Long id, Long newPoints, String token) {
        checkTokenIsCorrect(id, token);
        return allUsers.updatePointsById(id, newPoints);
    }

    @Override
    public User updateUsername(String username, String newUsername, String token) {
        var id = allUsers.getUserByUsername(username).getId();
        checkTokenIsCorrect(id, token);
        registrationService.checkLoginIsValid(newUsername);
        return allUsers.updateUsernameById(id, newUsername);
    }

    @Override
    public User updatePassword(String username, String newPassword, String token) {
        var id = allUsers.getUserByUsername(username).getId();
        checkTokenIsCorrect(id, token);
        registrationService.checkPasswordIsValid(newPassword);
        return allUsers.updatePasswordById(id, newPassword);
    }

    @Override
    public User updatePoints(String username, Long newPoints, String token) {
        var id = allUsers.getUserByUsername(username).getId();
        checkTokenIsCorrect(id, token);
        return allUsers.updatePointsById(id, newPoints);
    }
}
