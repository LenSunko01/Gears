package ru.hse.gears.service.user;

import ru.hse.gears.models.dto.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    /*
        Returns user with the given id/username
        Throws UserBaseException if user with given id/username is not found
        Throws AuthenticationException if token is invalid
    */
    User getUserById(Long id, String token);
    User getUserByUsername(String username, String token);

    /*
        Returns random user entry (username and points)
        Returns null if no user exists
    */
    Map.Entry<String, Long> getRandomUser();

    /*
        Returns map with all users (username and points)
    */
    Map<String, Long> getAll();

    /*
        Returns list with first 'numberOfUsers' users sorted by rating
        If many users share the same number of points, then all of them are returned
        IllegalArgumentException is thrown if 'numberOfUsers' is less than 1
    */
    List<User> getSortedByRatingList(int numberOfUsers);

    /*
        Returns true if token corresponds to user with given id and false otherwise
    */
    boolean validateToken(Long id, String token);

    /*
        Throws AuthenticationException if token is invalid
        Throws InvalidLoginException if new username is invalid
        Throws UserBaseException if user with given id/username is not found
    */
    User updateUsername(Long id, String newUsername, String token);
    User updateUsername(String username, String newUsername, String token);

    /*
        Throws AuthenticationException if token is invalid
        Throws InvalidPasswordException if new password is invalid
        Throws UserBaseException if user with given id/username is not found
    */
    User updatePassword(Long id, String newPassword, String token);
    User updatePassword(String password, String newPassword, String token);

    /*
        Throws AuthenticationException if token is invalid
        Throws UserBaseException if user with given id/username is not found
    */
    User updatePoints(Long id, Long newPoints, String token);
    User updatePoints(String username, Long newPoints, String token);

    /*
        Throws AuthenticationException if token is invalid
        Throws IllegalArgumentException if 'newPoints' is a negative number
        Throws UserBaseException if user with given id is not found
    */
    User updatePicture(Long id, byte[] newPicture, String token);

    /*
       Throws AuthenticationException if token is invalid
       Throws UserBaseException if user with given id is not found
    */
    byte[] getPictureById(Long id, String token);
}
