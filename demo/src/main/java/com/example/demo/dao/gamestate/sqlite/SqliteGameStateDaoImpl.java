package com.example.demo.dao.gamestate.sqlite;

import com.example.demo.dao.gamestate.GameStateDao;
import com.example.demo.models.dto.GameState;
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

@Repository
@Primary
public class SqliteGameStateDaoImpl implements GameStateDao {
    private static String DB_FILE_NAME = "gameState.sql";

    private static String GET_STATE_BY_STATE_ID = "select * from game_state where id = ?";

    private static String GET_STATE_BY_USER_ID = "select game_id from user_to_game where user_id = ?";

    private static String GET_ALL_STATES = "select * from game_state";

    private static String INSERT_GAME_STATE = "insert into game_state(gear, first_score, second_score) values (?, ?, ?)";

    private static String INSERT_USER = "insert into user_to_game(user_id, game_id) values (?, ?)";

    Connection conn;

    @PostConstruct
    void initConnection() {
        this.conn = SqliteUtils.connect(DB_FILE_NAME);
    }


    @Override
    public GameState getStateById(Long id) {
        PreparedStatement getGameStateStatement = null;
        try {
            getGameStateStatement = conn.prepareStatement(GET_STATE_BY_STATE_ID);

            getGameStateStatement.setLong(1, id);
            ResultSet rs = getGameStateStatement.executeQuery();


            return getGameState(rs);


        } catch (SQLException e) {
            e.printStackTrace();
        }

        //DO NOT FUCKING RETURN FUCKING NULL!!!!
        //THROW USEFUL EXCEPTION!!!!!
        return null;
    }

    @Override
    public GameState getStateByUserId(Long id) {
        PreparedStatement getGameStateStatement = null;
        try {
            getGameStateStatement = conn.prepareStatement(GET_STATE_BY_USER_ID);

            getGameStateStatement.setLong(1, id);
            ResultSet rs = getGameStateStatement.executeQuery();
            return getStateById(rs.getLong("game_id"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        //DO NOT FUCKING RETURN FUCKING NULL!!!!
        //THROW USEFUL EXCEPTION!!!!!
        return null;
    }

    private GameState getGameState(ResultSet rs) throws SQLException {
        return new GameState(
                rs.getLong("id"),
                rs.getInt("gear"),
                rs.getInt("first_score"),
                rs.getInt("second_score")
        );
    }

    @Override
    public GameState saveGameState(GameState game) {
        PreparedStatement insertGameStateStmt = null;
        try {

            insertGameStateStmt = conn.prepareStatement(INSERT_GAME_STATE);

            insertGameStateStmt.setInt(1, game.getNumberOfActiveGear());
            insertGameStateStmt.setInt(2, game.getScoreOfFirstPlayer());
            insertGameStateStmt.setInt(3, game.getScoreOfSecondPlayer());

            insertGameStateStmt.execute();
            ResultSet rs = insertGameStateStmt.getGeneratedKeys();
            return this.getStateById(rs.getLong(1));


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<GameState> getAll() {
        PreparedStatement getAllGameStatesStmt = null;
        List<GameState> states = new ArrayList<>();
        try {
            getAllGameStatesStmt = conn.prepareStatement(GET_ALL_STATES);
            ResultSet rs = getAllGameStatesStmt.executeQuery();
            while (rs.next()) {
                states.add(getGameState(rs)
                );
            }

            return states;


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addPlayersToGame(Long idGame, Long idUserOne, Long idUserSecond) {
        PreparedStatement insertGameStateStmt = null;
        try {

            insertGameStateStmt = conn.prepareStatement(INSERT_USER);

            insertGameStateStmt.setLong(1, idUserOne);
            insertGameStateStmt.setLong(2, idGame);

            insertGameStateStmt.execute();

            insertGameStateStmt.setLong(1, idUserSecond);
            insertGameStateStmt.setLong(2, idGame);

            insertGameStateStmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}