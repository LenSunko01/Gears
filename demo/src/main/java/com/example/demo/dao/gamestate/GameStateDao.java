package com.example.demo.dao.gamestate;

import com.example.demo.models.dto.GameState;

import java.util.List;


public interface GameStateDao {
    GameState getStateById(Long id);
    GameState getStateByUserId(Long id);
    GameState saveGameState(GameState game);
    List<GameState> getAll();
    void addPlayersToGame(Long idGame, Long idUserOne, Long idUserSecond);
}
