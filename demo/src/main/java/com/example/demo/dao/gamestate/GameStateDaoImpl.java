package com.example.demo.dao.gamestate;

import com.example.demo.models.dto.GameState;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/*
Remember about concurrency (!)
 */


@Repository
public class GameStateDaoImpl implements GameStateDao {

    Map<Long, GameState> gameStateStorage = new HashMap<>();
    Map<Long, Long> usersToGame = new HashMap<>();

    @Override
    public GameState getStateById(Long id) {
        return gameStateStorage.get(id);
    }

    @Override
    public GameState getStateByUserId(Long id) {
        return gameStateStorage.get(usersToGame.get(id));
    }

    @Override
    public GameState saveStateGame(GameState game) {
        long id = generateGameId();
        game.setId(id);
        gameStateStorage.put(game.getId(), game);
        return game;
    }

    @Override
    public List<GameState> getAll() {
        return new ArrayList<>(gameStateStorage.values());
    }

    @Override
    public void setGame(Long idGame, Long idUserOne, Long idUserSecond, GameState game) {
        usersToGame.put(idUserOne,idGame);
        usersToGame.put(idUserSecond,idGame);
        gameStateStorage.put(idGame, game);
    }
    @Override
    public Long generateGameId() {
        long id = new Random().nextLong();
        while (gameStateStorage.containsKey(id)) {
            id = new Random().nextLong();
        }
        return id;
    }

}
