package ru.hse.gears.service.user;

import ru.hse.gears.dao.allusers.AllUsersDao;
import ru.hse.gears.models.dto.User;
import ru.hse.gears.service.registration.RegistrationService;
import ru.hse.gears.web.controllers.UserController;
import ru.hse.gears.web.exceptions.AuthenticationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private static final Log logger = LogFactory.getLog(UserController.class);
    private final AllUsersDao allUsers;
    private final RegistrationService registrationService;

    public UserServiceImpl(
            AllUsersDao allUsers,
            RegistrationService registrationService) {
        this.allUsers = allUsers;
        this.registrationService = registrationService;
    }

    @Override
    public boolean validateToken(Long id, String token) {
        var correctToken = allUsers.getTokenById(id);
        return correctToken.equals(token);
    }

    @Override
    public User getUserById(Long id, String token) {
        if (!validateToken(id, token)) {
            throw new AuthenticationException();
        }
        return allUsers.getUserById(id);
    }

    @Override
    public Map.Entry<String, Long> getRandomUser() {
        Map<String, Long> map = allUsers.getAll();
        if (map.isEmpty()) {
            return null;
        }
        var list = new ArrayList<>(map.entrySet());
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    @Override
    public Map<String, Long> getAll() {
        return allUsers.getAll();
    }

    @Override
    public List<User> getSortedByRatingList(int numberOfUsers) {
        if (numberOfUsers <= 0) {
            throw new IllegalArgumentException("Required number of users must be at least 1");
        }
        var list = allUsers.getSortedByRatingList();
        var pointsInLastPosition = list.get(numberOfUsers).getPoints();
        return list.stream().takeWhile(user -> user.getPoints() >= pointsInLastPosition).collect(Collectors.toList());
    }

    @Override
    public User updateUsername(Long id, String newUsername, String token) {
        if (!validateToken(id, token)) {
            throw new AuthenticationException();
        }
        registrationService.checkLoginIsValid(newUsername);
        return allUsers.updateUsernameById(id, newUsername);
    }

    @Override
    public User updatePassword(Long id, String newPassword, String token) {
        if (!validateToken(id, token)) {
            throw new AuthenticationException();
        }
        registrationService.checkPasswordIsValid(newPassword);
        return allUsers.updatePasswordById(id, newPassword);
    }

    @Override
    public User updatePoints(Long id, Long newPoints, String token) {
        if (!validateToken(id, token)) {
            throw new AuthenticationException();
        }
        if (newPoints < 0) {
            throw new IllegalArgumentException("Points can not be negative");
        }
        return allUsers.updatePointsById(id, newPoints);
    }

    @Override
    public User updatePicture(Long id, byte[] newPicture, String token) {
        if (!validateToken(id, token)) {
            throw new AuthenticationException();
        }
        return allUsers.updatePictureById(id, newPicture);
    }

    @Override
    public User getUserByUsername(String username, String token) {
        var user = allUsers.getUserByUsername(username);
        var id = user.getId();
        if (!validateToken(id, token)) {
            throw new AuthenticationException();
        }
        return user;
    }

    @Override
    public User updateUsername(String username, String newUsername, String token) {
        var id = allUsers.getUserByUsername(username).getId();
        if (!validateToken(id, token)) {
            throw new AuthenticationException();
        }
        registrationService.checkLoginIsValid(newUsername);
        return allUsers.updateUsernameById(id, newUsername);
    }

    @Override
    public User updatePassword(String username, String newPassword, String token) {
        var id = allUsers.getUserByUsername(username).getId();
        if (!validateToken(id, token)) {
            throw new AuthenticationException();
        }
        registrationService.checkPasswordIsValid(newPassword);
        return allUsers.updatePasswordById(id, newPassword);
    }

    @Override
    public User updatePoints(String username, Long newPoints, String token) {
        var id = allUsers.getUserByUsername(username).getId();
        if (!validateToken(id, token)) {
            throw new AuthenticationException();
        }
        if (newPoints < 0) {
            throw new IllegalArgumentException("Points can not be negative");
        }
        return allUsers.updatePointsById(id, newPoints);
    }

    @Override
    public byte[] getPictureById(Long id, String token) {
        if (!validateToken(id, token)) {
            throw new AuthenticationException();
        }
        return allUsers.getPictureById(id);
    }
}
