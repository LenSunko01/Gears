package ru.hse.gears.dao.gamestate;

import ru.hse.gears.models.dto.Board;
import ru.hse.gears.models.dto.GameState;


public interface GameStateDao {
    GameState getStateById(Long id);
    GameState saveGameState(GameState game);
    GameState updateGameState(Long id, GameState newGameState);
    GameState updateBoardInGameState(Long id, GameState.CurrentPlayer player, Board board);
    boolean checkGameExists(Long id);
    void deleteGame(Long id);
}
