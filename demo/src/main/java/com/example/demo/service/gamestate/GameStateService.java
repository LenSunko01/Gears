package com.example.demo.service.gamestate;

import com.example.demo.models.dto.Board;
import com.example.demo.models.dto.GameState;
import com.example.demo.models.dto.User;

import java.util.List;

public interface GameStateService {
    boolean checkGameExists(Long id);
    Long setGame(User firstUser, User secondUser);
    GameState getStateById(Long id, String token);
    GameState updateStateById(Long id, String token, GameState newGameState);
    GameState updateBoardById(Long id, String token, GameState.CurrentPlayer player, Board board);
    void validateToken(Long id, String token);
    void deleteGameState(Long id, String token);
}
