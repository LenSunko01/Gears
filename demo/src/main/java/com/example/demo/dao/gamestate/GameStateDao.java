package com.example.demo.dao.gamestate;

import com.example.demo.models.dto.GameState;

import java.util.List;


public interface GameStateDao {
    GameState getStateById(Long id);
    GameState saveGameState(GameState game);
    GameState updateGameState(Long id, GameState newGameState);
    void deleteGame(Long id);
}
