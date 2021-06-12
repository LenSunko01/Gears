package com.example.demo.service.gamestate;

import com.example.demo.models.dto.Board;
import com.example.demo.models.dto.GameState;
import com.example.demo.models.dto.User;

public interface GameStateService {
    boolean checkGameExists(Long id);
    Long setGame(User firstUser, User secondUser);
    GameState getStateById(Long id, String token, GameState.CurrentPlayer player);
    GameState updateStateById(Long id, String token, GameState newGameState, GameState.CurrentPlayer player);
    GameState updateBoardById(Long id, String token, GameState.CurrentPlayer player, Board board);
    boolean validateToken(Long id, String token, GameState.CurrentPlayer player);
    void deleteGameState(Long id, String token, GameState.CurrentPlayer player);
}
