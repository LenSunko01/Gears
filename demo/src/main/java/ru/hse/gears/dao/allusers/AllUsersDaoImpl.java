package ru.hse.gears.dao.allusers;

import ru.hse.gears.models.dto.User;
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
    Map<User, Long> userToId = new HashMap<>();
    Map<User, byte[]> userToPicture = new HashMap<>();
    Map<byte[], User> pictureToUser = new HashMap<>();
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
    public boolean updateToken(String token, String username) {
        var user = usernameToUser.get(username);
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
    public Long getIdByUsername(String username) {
        return userToId.get(usernameToUser.get(username));
    }

    @Override
    public User updateTotalGamesById(Long id, Long newTotalGames) {
        var user = idToUser.get(id);
        if (user == null) {
            return null;
        }
        var newUser = new User(user);
        newUser.setTotalNumberOfGames(newTotalGames);
        updateUser(user, newUser);
        return newUser;
    }

    @Override
    public User updateGamesWonById(Long id, Long newGamesWon) {
        var user = idToUser.get(id);
        if (user == null) {
            return null;
        }
        var newUser = new User(user);
        newUser.setNumberOfGamesWon(newGamesWon);
        updateUser(user, newUser);
        return newUser;
    }

    @Override
    public User updateGamesLostById(Long id, Long newGamesLost) {
        var user = idToUser.get(id);
        if (user == null) {
            return null;
        }
        var newUser = new User(user);
        newUser.setNumberOfGamesLost(newGamesLost);
        updateUser(user, newUser);
        return newUser;
    }

    @Override
    public User updatePictureByUsername(String username, byte[] picture) {
        var user = usernameToUser.get(username);
        if (user == null) {
            return null;
        }
        var newUser = new User(user);
        var oldPicture = userToPicture.get(newUser);
        userToPicture.replace(newUser, picture);
        pictureToUser.remove(oldPicture);
        pictureToUser.put(picture, newUser);
        updateUser(user, newUser);
        return newUser;
    }

    @Override
    public User updatePictureById(Long id, byte[] picture) {
        var username = idToUser.get(id).getUsername();
        return updatePictureByUsername(username, picture);
    }

    @Override
    public byte[] getPictureByUsername(String username) {
        return userToPicture.get(usernameToUser.get(username));
    }

    @Override
    public byte[] getPictureById(Long id) {
        return userToPicture.get(idToUser.get(id));
    }

    @Override
    public User getUserById(Long id) {
        return idToUser.get(id);
    }

    @Override
    public Map<String, Long> getAll() {
        Map<String, Long> users = new HashMap<>();
        for (var user : idToUser.values()) {
            users.put(user.getUsername(), user.getPoints());
        }
        return users;
    }

    @Override
    public ArrayList<User> getSortedByRatingList() {
        var list = new ArrayList<>(userToId.keySet());
        list.sort(Comparator.comparingLong((User::getPoints)));
        return list;
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
    public String getTokenById(Long id) {
        return getTokenByUsername(idToUser.get(id).getUsername());
    }

    @Override
    public User getUserByToken(String token) {
        return tokenToUser.get(token);
    }

    private void updateUser(User user, User newUser) {
        userToUsername.remove(user);
        userToUsername.put(newUser, newUser.getUsername());
        usernameToUser.remove(user.getUsername());
        usernameToUser.put(newUser.getUsername(), newUser);

        var token = userToToken.get(user);
        userToToken.remove(user);
        userToToken.put(newUser, token);
        tokenToUser.replace(token, newUser);

        userToId.remove(user);
        userToId.put(newUser, newUser.getId());
        idToUser.remove(user.getId());
        idToUser.put(newUser.getId(), newUser);
        var picture = userToPicture.get(user);
        userToPicture.remove(user);
        userToPicture.put(newUser, picture);
        pictureToUser.replace(picture, newUser);
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
        var newUser = new User(user);
        newUser.setUsername(newUsername);
        updateUser(user, newUser);
        return newUser;
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
        var newUser = new User(user);
        user.setPassword(newPassword);
        updateUser(user, newUser);
        return newUser;
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
        updateUser(user, newUser);
        return newUser;
    }

    @Override
    public User updatePointsByUsername(String username, Long newPoints) {
        var id = usernameToUser.get(username).getId();
        return updatePointsById(id, newPoints);
    }

    @Override
    public User addUser(String username, String password, String token, Long points) {
        if (checkUsernameExists(username)) {
            return null;
        }

        var user = new User(generateUserId(), username, password, points,
                0L, 0L, 0L);
        usernameToUser.put(username, user);
        userToUsername.put(user, username);
        usernameToPassword.put(username, password);
        tokenToUser.put(token, user);
        userToToken.put(user, token);
        idToUser.put(user.getId(), user);
        userToId.put(user, user.getId());
        return user;
    }

    @Override
    public boolean checkPasswordIsCorrect(String username, String password) {
        return Objects.equals(usernameToPassword.get(username), password);
    }
}
