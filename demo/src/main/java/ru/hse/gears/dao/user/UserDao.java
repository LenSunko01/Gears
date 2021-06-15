package ru.hse.gears.dao.user;

import ru.hse.gears.models.dto.User;

import java.util.ArrayList;
import java.util.Map;

public interface UserDao {
    User getUserByUsername(String username);

    boolean checkUsernameExists(String username);

    User addUser(String username, String password, String token, Long points);

    boolean checkPasswordIsCorrect(String username, String password);

    boolean checkTokenExists(String token);

    User getUserById(Long id);

    User getUserByToken(String token);

    Map<String, Long> getAll();

    ArrayList<User> getSortedByRatingList();

    String getTokenByUsername(String username);

    String getTokenById(Long id);

    User updateUsernameById(Long id, String newUsername);

    User updateUsernameByUsername(String prevUsername, String newUsername);

    User updatePasswordById(Long id, String newPassword);

    User updatePasswordByUsername(String username, String newPassword);

    User updatePointsById(Long id, Long newPoints);

    User updatePointsByUsername(String username, Long newPoints);

    boolean updateToken(String token, String username);

    Long getIdByUsername(String username);

    User updateTotalGamesById(Long id, Long newTotalGames);

    User updateGamesWonById(Long id, Long newGamesWon);

    User updateGamesLostById(Long id, Long newGamesLost);

    User updatePictureByUsername(String username, byte[] picture);

    User updatePictureById(Long id, byte[] picture);

    byte[] getPictureByUsername(String username);

    byte[] getPictureById(Long id);
}
