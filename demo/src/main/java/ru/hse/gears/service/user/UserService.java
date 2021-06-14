package ru.hse.gears.service.user;

import ru.hse.gears.models.dto.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    User getUserById(Long id, String token);
    Map.Entry<String, Long> getRandomUser();

    Map<String, Long> getAll();

    List<User> getSortedByRatingList(int numberOfUsers);

    boolean validateToken(Long id, String token);

    User updateUsername(Long id, String newUsername, String token);

    User updatePassword(Long id, String newPassword, String token);

    User updatePoints(Long id, Long newPoints, String token);

    User updatePicture(Long id, byte[] newPicture, String token);

    User getUserByUsername(String username, String token);

    User updateUsername(String username, String newUsername, String token);

    User updatePassword(String password, String newPassword, String token);

    User updatePoints(String username, Long newPoints, String token);

    byte[] getPictureById(Long id, String token);
}
