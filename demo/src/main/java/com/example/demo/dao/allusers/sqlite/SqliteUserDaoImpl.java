package com.example.demo.dao.allusers.sqlite;

import com.example.demo.dao.allusers.AllUsersDao;
import com.example.demo.models.dto.User;
import com.example.demo.utils.SqliteUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@Primary
public class SqliteUserDaoImpl implements AllUsersDao {
    private static String DB_FILE_NAME = "gameState.sql";

    private static String GET_USER_BY_USERNAME = "select * from user_state where user_login = ?";

    private static String GET_TOKEN_BY_USERNAME = "select user_token from user_state where user_login = ?";

    private static String GET_USER_BY_TOKEN = "select * from user_state where user_token = ?";

    private static String INSERT_USER = "insert into user_state(user_login, user_password, " +
            "user_token, points) values (?, ?, ?, ?)";

    private static String GET_USER_BY_USER_ID = "select * from user_state where id = ?";

    private static String GET_ALL_USERS = "select * from user_state";

    private static String INSERT_TOKEN = "update user_state set user_token=? where user_login=?";

    private static String INSERT_USERNAME = "update user_state set user_login=? where id=?";

    private static String INSERT_PASSWORD = "update user_state set user_password=? where id=?";

    private static String INSERT_POINTS = "update user_state set user_points=? where id=?";

    private static String DELETE_USER = "delete from user_state where id=?";

    Connection conn;

    public SqliteUserDaoImpl(SqliteUtils utils) {
        this.conn = utils.getConnection(DB_FILE_NAME);
    }

    private User getUserByQuery(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("user_login"),
                rs.getString("user_password"),
                rs.getLong("points")
        );
    }

    @Override
    public User getUserByUsername(String username) {
        PreparedStatement getUserStmt = null;
        try {
            getUserStmt = conn.prepareStatement(GET_USER_BY_USERNAME);

            getUserStmt.setString(1, username);
            ResultSet rs = getUserStmt.executeQuery();
            return getUserByQuery(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean checkUsernameExists(String username) {
        PreparedStatement checkUserStmt = null;
        try {
            checkUserStmt = conn.prepareStatement(GET_USER_BY_USERNAME);
            checkUserStmt.setString(1, username);
            var resSet = checkUserStmt.executeQuery();
            return resSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public User addUser(String username, String password, String token) {
        PreparedStatement addUserStmt = null;
        try {
            addUserStmt = conn.prepareStatement(INSERT_USER);

            addUserStmt.setString(1, username);
            addUserStmt.setString(2, password);
            addUserStmt.setString(3, token);
            addUserStmt.setLong(4, 0);

            addUserStmt.execute();
            ResultSet rs = addUserStmt.getGeneratedKeys();
            return getUserById(rs.getLong(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean checkPassword(String username, String password) {
        PreparedStatement checkPasswordStmt = null;
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
    public List<User> getAll() {
        PreparedStatement getAllUsersStmt = null;
        List<User> users = new ArrayList<>();
        try {
            getAllUsersStmt = conn.prepareStatement(GET_ALL_USERS);
            ResultSet rs = getAllUsersStmt.executeQuery();
            while (rs.next()) {
                users.add(getUserByQuery(rs));
            }

            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getTokenByUsername(String username) {
        PreparedStatement getUserStmt = null;
        try {
            getUserStmt = conn.prepareStatement(GET_TOKEN_BY_USERNAME);

            getUserStmt.setString(1, username);
            ResultSet rs = getUserStmt.executeQuery();
            return rs.getString("user_token");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public User getUserByToken(String token) {
        PreparedStatement getUserStmt = null;
        try {
            getUserStmt = conn.prepareStatement(GET_USER_BY_TOKEN);

            getUserStmt.setString(1, token);
            ResultSet rs = getUserStmt.executeQuery();
            return getUserByQuery(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public User updateUsernameById(Long id, String newUsername) {
        PreparedStatement insertGameStateStmt = null;
        try {
            insertGameStateStmt = conn.prepareStatement(INSERT_USERNAME);

            insertGameStateStmt.setLong(1, id);
            insertGameStateStmt.setString(2, newUsername);

            insertGameStateStmt.execute();
            ResultSet rs = insertGameStateStmt.getGeneratedKeys();
            return getUserById(rs.getLong(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public User updatePasswordById(Long id, String newPassword) {
        PreparedStatement insertGameStateStmt = null;
        try {
            insertGameStateStmt = conn.prepareStatement(INSERT_PASSWORD);

            insertGameStateStmt.setLong(1, id);
            insertGameStateStmt.setString(2, newPassword);

            insertGameStateStmt.execute();
            ResultSet rs = insertGameStateStmt.getGeneratedKeys();
            return getUserById(rs.getLong(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public User updatePointsById(Long id, Long newPoints) {
        PreparedStatement insertGameStateStmt = null;
        try {
            insertGameStateStmt = conn.prepareStatement(INSERT_POINTS);

            insertGameStateStmt.setLong(1, id);
            insertGameStateStmt.setLong(2, newPoints);

            insertGameStateStmt.execute();
            ResultSet rs = insertGameStateStmt.getGeneratedKeys();
            return getUserById(rs.getLong(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean deleteUser(String username) {
        Long id = getUserByUsername(username).getId();
        PreparedStatement insertGameStateStmt = null;
        try {
            insertGameStateStmt = conn.prepareStatement(DELETE_USER);

            insertGameStateStmt.setLong(1, id);

            insertGameStateStmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean checkTokenExists(String token) {
        PreparedStatement checkTokenStmt = null;
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
    public boolean updateToken(String token, User user) {
        PreparedStatement updateTokenStmt = null;
        try {

            updateTokenStmt = conn.prepareStatement(INSERT_TOKEN);

            updateTokenStmt.setString(1, token);
            updateTokenStmt.setString(2, user.getUsername());

            updateTokenStmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public User getUserById(Long id) {
        PreparedStatement userStateStmt = null;
        try {
            userStateStmt = conn.prepareStatement(GET_USER_BY_USER_ID);

            userStateStmt.setLong(1, id);
            ResultSet rs = userStateStmt.executeQuery();
            return getUserByQuery(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //DO NOT FUCKING RETURN FUCKING NULL!!!!
        //THROW USEFUL EXCEPTION!!!!!
        return null;
    }
}