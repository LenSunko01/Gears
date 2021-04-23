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

    @Override
    public GameState getStateById(Long id) {
        return gameStateStorage.get(id);
    }

    @Override
    public GameState saveGameState(GameState game) {
        long id = generateGameId();
        game.setId(id);
        gameStateStorage.put(id, game);
        return game;
    }

    @Override
    public GameState updateGameState(Long id, GameState newGameState) {
        gameStateStorage.replace(id, newGameState);
        return newGameState;
    }

    @Override
    public void deleteGame(Long id) {
        gameStateStorage.remove(id);
    }

    private Long generateGameId() {
        long id = new Random().nextLong();
        while (gameStateStorage.containsKey(id)) {
            id = new Random().nextLong();
        }
        return id;
    }

}
