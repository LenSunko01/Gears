package ru.hse.gears.dao.gamestate;

import ru.hse.gears.models.dto.Board;
import ru.hse.gears.models.dto.GameState;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
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

    /* concurrency! */
    @Override
    public GameState updateBoardInGameState(Long id, GameState.CurrentPlayer player, Board board) {
        var gameState = gameStateStorage.get(id);
        if (player.equals(GameState.CurrentPlayer.FIRSTPLAYER)) {
            gameState.setFirstPlayerHasInitializedBoard(true);
            gameState.setFirstPlayerBoard(board);
        } else {
            gameState.setSecondPlayerHasInitializedBoard(true);
            gameState.setSecondPlayerBoard(board);
        }
        gameStateStorage.replace(id, gameState);
        return gameStateStorage.get(id);
    }

    @Override
    public boolean checkGameExists(Long id) {
        return gameStateStorage.containsKey(id);
    }

    @Override
    public void deleteGame(Long id) {
        gameStateStorage.remove(id);
    }

    private Long generateGameId() {
        long id = new Random().nextLong();
        while (gameStateStorage.containsKey(id) || id <= 0) {
            id = new Random().nextLong();
        }
        return id;
    }

}
