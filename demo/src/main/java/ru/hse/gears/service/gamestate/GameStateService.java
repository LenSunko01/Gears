package ru.hse.gears.service.gamestate;

import ru.hse.gears.models.dto.Board;
import ru.hse.gears.models.dto.GameState;
import ru.hse.gears.models.dto.User;

public interface GameStateService {
    /*
        Returns true if game with given id exists and false otherwise
    */
    boolean checkGameExists(Long id);

    /*
        Creates a new game with provided users in the specified order
        Returns newly created game id
    */
    Long setGame(User firstUser, User secondUser);

    /*
        Returns null if game with given id does not exist
        Throws AuthenticationException if token and player do not correspond
    */
    GameState getStateById(Long id, String token, GameState.CurrentPlayer player);

    /*
        Throws IllegalArgumentException if game with provided id does not exist
        Throws AuthenticationException if token and player or player and game do not correspond
    */
    GameState updateStateById(Long id, String token, GameState newGameState, GameState.CurrentPlayer player);
    GameState updateBoardById(Long id, String token, GameState.CurrentPlayer player, Board board);

    /*
        Throws IllegalArgumentException if game with provided id does not exist
        Returns false if player does not belong to the game with provided id
        Returns false if token and player do not correspond
        Returns true otherwise
    */
    boolean validateToken(Long id, String token, GameState.CurrentPlayer player);

    /*
        Throws AuthenticationException if token and player or player and game do not correspond
        Deletes game after both players have invoked this method
    */
    void deleteGameState(Long id, String token, GameState.CurrentPlayer player);
}
