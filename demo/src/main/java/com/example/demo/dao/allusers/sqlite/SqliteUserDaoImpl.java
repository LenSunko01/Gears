package com.example.demo.dao.allusers.sqlite;

import com.example.demo.dao.allusers.AllUsersDao;
import com.example.demo.models.dto.User;
import com.example.demo.utils.SqliteUtils;
import com.example.demo.web.controllers.UserController;
import com.example.demo.web.exceptions.InvalidUsernameException;
import com.example.demo.web.exceptions.SQLUserBaseException;
import com.example.demo.web.exceptions.UserNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Repository
@Primary
public class SqliteUserDaoImpl implements AllUsersDao {
    private static final Log logger = LogFactory.getLog(UserController.class);
    private static final String DB_FILE_NAME = "gameState.sql";

    private static final String GET_USER_BY_USERNAME = "select * from user_state where user_login = ?";

    private static final String GET_TOKEN_BY_USERNAME = "select user_token from user_state where user_login = ?";

    private static final String GET_ID_BY_USERNAME = "select id from user_state where user_login = ?";

    private static final String GET_USER_BY_TOKEN = "select * from user_state where user_token = ?";

    private static final String INSERT_USER = "insert into user_state(user_login, user_password, " +
            "user_token, points, total_games, games_won, games_lost, picture) values (?, ?, ?, ?, ?, ?, ?,?)";

    private static final String GET_USER_BY_USER_ID = "select * from user_state where id = ?";

    private static final String GET_ALL_USERS = "select * from user_state";

    private static final String GET_PICTURE_BY_USERNAME = "select picture from user_state where user_login = ?";

    private static final String INSERT_TOKEN_BY_USERNAME = "update user_state set user_token=? where user_login=?";

    private static final String INSERT_USERNAME_BY_ID = "update user_state set user_login=? where id=?";

    private static final String INSERT_USERNAME_BY_USERNAME = "update user_state set user_login=? where user_login=?";

    private static final String INSERT_PASSWORD_BY_ID = "update user_state set user_password=? where id=?";

    private static final String INSERT_PASSWORD_BY_USERNAME = "update user_state set user_password=? where user_login=?";

    private static final String INSERT_POINTS_BY_ID = "update user_state set points=? where id=?";

    private static final String INSERT_POINTS_BY_USERNAME = "update user_state set points=? where user_login=?";

    private static final String INSERT_TOTAL_GAMES_BY_ID = "update user_state set total_games=? where id=?";

    private static final String INSERT_GAMES_WON_BY_ID = "update user_state set games_won=? where id=?";

    private static final String INSERT_GAMES_LOST_BY_ID = "update user_state set games_lost=? where id=?";

    private static final String INSERT_PICTURE_BY_USERNAME = "update user_state set picture=? where user_login=?";

    Connection conn;

    public SqliteUserDaoImpl(SqliteUtils utils) {
        this.conn = utils.getConnection(DB_FILE_NAME);
    }

    private User getUserByQuery(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("user_login"),
                rs.getString("user_password"),
                rs.getLong("points"),
                rs.getLong("total_games"),
                rs.getLong("games_won"),
                rs.getLong("games_lost"));
    }

    @Override
    public User getUserByUsername(String username) {
        PreparedStatement getUserStmt;
        try {
            logger.info("Getting user " + username + "from database");
            getUserStmt = conn.prepareStatement(GET_USER_BY_USERNAME);
            getUserStmt.setString(1, username);
            ResultSet rs = getUserStmt.executeQuery();
            logger.info("Got ResultSet");
            return getUserByQuery(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new UserNotFoundException(e);
        }
    }

    @Override
    public boolean checkUsernameExists(String username) {
        PreparedStatement checkUserStmt;
        try {
            checkUserStmt = conn.prepareStatement(GET_USER_BY_USERNAME);
            checkUserStmt.setString(1, username);
            var resSet = checkUserStmt.executeQuery();
            return resSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new UserNotFoundException(e);
        }
    }

    @Override
    public User addUser(String username, String password, String token, Long points) {
        PreparedStatement addUserStmt;
        try {
            addUserStmt = conn.prepareStatement(INSERT_USER);
            addUserStmt.setString(1, username);
            addUserStmt.setString(2, password);
            addUserStmt.setString(3, token);
            addUserStmt.setLong(4, points);
            addUserStmt.setLong(5, 0);
            addUserStmt.setLong(6, 0);
            addUserStmt.setLong(7, 0);
            addUserStmt.setBytes(8, null);
            addUserStmt.execute();
            ResultSet rs = addUserStmt.getGeneratedKeys();
            return getUserById(rs.getLong(1));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLUserBaseException(e);
        }
    }

    @Override
    public boolean checkPasswordIsCorrect(String username, String password) {
        PreparedStatement checkPasswordStmt;
        try {
            checkPasswordStmt = conn.prepareStatement(GET_USER_BY_USERNAME);
            checkPasswordStmt.setString(1, username);
            ResultSet rs = checkPasswordStmt.executeQuery();
            return Objects.equals(rs.getString("user_password"), password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Map<String, Long> getAll() {
        PreparedStatement getAllUsersStmt;
        Map<String, Long> users = new HashMap<>();
        try {
            getAllUsersStmt = conn.prepareStatement(GET_ALL_USERS);
            ResultSet rs = getAllUsersStmt.executeQuery();
            while (rs.next()) {
                users.put(rs.getString("user_login"), rs.getLong("points"));
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLUserBaseException(e);
        }
    }

    @Override
    public String getTokenByUsername(String username) {
        PreparedStatement getUserStmt;
        try {
            getUserStmt = conn.prepareStatement(GET_TOKEN_BY_USERNAME);
            getUserStmt.setString(1, username);
            ResultSet rs = getUserStmt.executeQuery();
            return rs.getString("user_token");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLUserBaseException(e);
        }
    }

    @Override
    public User getUserByToken(String token) {
        PreparedStatement getUserStmt;
        try {
            getUserStmt = conn.prepareStatement(GET_USER_BY_TOKEN);
            getUserStmt.setString(1, token);
            ResultSet rs = getUserStmt.executeQuery();
            return getUserByQuery(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLUserBaseException(e);
        }
    }

    @Override
    public User updateUsernameById(Long id, String newUsername) {
        if (checkUsernameExists(newUsername)) {
            throw new InvalidUsernameException("User with provided username already exists");
        }
        PreparedStatement insertGameStateStmt;
        return prepareUserStmt(id, newUsername, INSERT_USERNAME_BY_ID);
    }

    private User prepareUserStmt(Long id, String newUsername, String insertUsernameById) {
        PreparedStatement insertGameStateStmt;
        try {
            insertGameStateStmt = conn.prepareStatement(insertUsernameById);
            insertGameStateStmt.setString(1, newUsername);
            insertGameStateStmt.setLong(2, id);

            insertGameStateStmt.execute();
            return getUserById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLUserBaseException(e);
        }
    }

    @Override
    public User updateUsernameByUsername(String prevUsername, String newUsername) {
        if (checkUsernameExists(newUsername)) {
            throw new InvalidUsernameException("User with provided username already exists");
        }
        PreparedStatement insertGameStateStmt;
        try {
            insertGameStateStmt = conn.prepareStatement(INSERT_USERNAME_BY_USERNAME);

            insertGameStateStmt.setString(1, newUsername);
            insertGameStateStmt.setString(2, prevUsername);

            insertGameStateStmt.execute();
            return getUserByUsername(newUsername);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLUserBaseException(e);
        }
    }

    @Override
    public User updatePasswordById(Long id, String newPassword) {
        PreparedStatement insertGameStateStmt;
        return prepareUserStmt(id, newPassword, INSERT_PASSWORD_BY_ID);
    }

    @Override
    public User updatePasswordByUsername(String username, String newPassword) {
        PreparedStatement insertGameStateStmt;
        try {
            insertGameStateStmt = conn.prepareStatement(INSERT_PASSWORD_BY_USERNAME);
            insertGameStateStmt.setString(1, newPassword);
            insertGameStateStmt.setString(2, username);
            insertGameStateStmt.execute();
            return getUserByUsername(username);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLUserBaseException(e);
        }
    }

    @Override
    public User updatePointsById(Long id, Long newPoints) {
        PreparedStatement insertGameStateStmt;
        try {
            insertGameStateStmt = conn.prepareStatement(INSERT_POINTS_BY_ID);
            insertGameStateStmt.setLong(1, newPoints);
            insertGameStateStmt.setLong(2, id);
            insertGameStateStmt.execute();
            return getUserById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLUserBaseException(e);
        }
    }

    @Override
    public User updateTotalGamesById(Long id, Long newTotalGames) {
        PreparedStatement insertGameStateStmt;
        try {
            insertGameStateStmt = conn.prepareStatement(INSERT_TOTAL_GAMES_BY_ID);
            insertGameStateStmt.setLong(1, newTotalGames);
            insertGameStateStmt.setLong(2, id);
            insertGameStateStmt.execute();
            return getUserById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User updateGamesWonById(Long id, Long newGamesWon) {
        PreparedStatement insertGameStateStmt;
        try {
            insertGameStateStmt = conn.prepareStatement(INSERT_GAMES_WON_BY_ID);
            insertGameStateStmt.setLong(1, newGamesWon);
            insertGameStateStmt.setLong(2, id);
            insertGameStateStmt.execute();
            return getUserById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User updateGamesLostById(Long id, Long newGamesLost) {
        PreparedStatement insertGameStateStmt;
        try {
            insertGameStateStmt = conn.prepareStatement(INSERT_GAMES_LOST_BY_ID);
            insertGameStateStmt.setLong(1, newGamesLost);
            insertGameStateStmt.setLong(2, id);
            insertGameStateStmt.execute();
            return getUserById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User updatePicture(String username, byte[] picture) {
        PreparedStatement insertGameStateStmt;
        try {
            insertGameStateStmt = conn.prepareStatement(INSERT_PICTURE_BY_USERNAME);
            insertGameStateStmt.setBytes(1, picture);
            insertGameStateStmt.setString(2, username);
            insertGameStateStmt.execute();
            return getUserByUsername(username);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLUserBaseException(e);
        }
    }

    @Override
    public byte[] getPicture(String username) {
        PreparedStatement getUserStmt;
        try {
            getUserStmt = conn.prepareStatement(GET_PICTURE_BY_USERNAME);
            getUserStmt.setString(1, username);
            ResultSet rs = getUserStmt.executeQuery();
            return rs.getBytes("picture");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLUserBaseException(e);
        }
    }

    @Override
    public User updatePointsByUsername(String username, Long newPoints) {
        PreparedStatement insertGameStateStmt;
        try {
            insertGameStateStmt = conn.prepareStatement(INSERT_POINTS_BY_USERNAME);
            insertGameStateStmt.setLong(1, newPoints);
            insertGameStateStmt.setString(2, username);
            insertGameStateStmt.execute();
            return getUserByUsername(username);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLUserBaseException(e);
        }
    }

    @Override
    public boolean checkTokenExists(String token) {
        PreparedStatement checkTokenStmt;
        try {
            checkTokenStmt = conn.prepareStatement(GET_USER_BY_TOKEN);
            checkTokenStmt.setString(1, token);
            var resSet = checkTokenStmt.executeQuery();
            return resSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateToken(String token, String username) {
        if (checkTokenExists(token)) {
            throw new IllegalArgumentException("User with provided token already exists");
        }
        PreparedStatement updateTokenStmt;
        try {
            updateTokenStmt = conn.prepareStatement(INSERT_TOKEN_BY_USERNAME);
            updateTokenStmt.setString(1, token);
            updateTokenStmt.setString(2, username);
            updateTokenStmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLUserBaseException(e);
        }
    }

    @Override
    public Long getIdByUsername(String username) {
        PreparedStatement getUserStmt;
        try {
            getUserStmt = conn.prepareStatement(GET_ID_BY_USERNAME);
            getUserStmt.setString(1, username);
            ResultSet rs = getUserStmt.executeQuery();
            return rs.getLong("id");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLUserBaseException(e);
        }
    }

    @Override
    public User getUserById(Long id) {
        PreparedStatement userStateStmt;
        try {
            userStateStmt = conn.prepareStatement(GET_USER_BY_USER_ID);
            userStateStmt.setLong(1, id);
            ResultSet rs = userStateStmt.executeQuery();
            return getUserByQuery(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLUserBaseException(e);
        }
    }
}