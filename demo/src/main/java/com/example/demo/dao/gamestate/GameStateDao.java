package com.example.demo.dao.gamestate;

import com.example.demo.models.dto.GameState;

import java.util.List;

public interface GameStateDao {
    GameState getStateById(Long id);
    GameState getStateByUserId(Long id);
    GameState saveStateGame(GameState game);
    List<GameState> getAll();
    void setGame(Long idGame, Long idUserOne, Long idUserSecond, GameState game);
    Long generateGameId();
}
