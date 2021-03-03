package com.example.demo.service.game;

import com.example.demo.models.dto.GameState;
import com.example.demo.models.dto.User;
import com.example.demo.service.gamestate.GameStateService;
import com.example.demo.service.user.UserService;

import java.util.Arrays;
import java.util.List;

public class GameServiceImpl implements GameService {
    private final GameStateService gameStateService;
    private final UserService userService;

    public GameServiceImpl(GameStateService gameStateService, UserService userService) {
        this.gameStateService = gameStateService;
        this.userService = userService;
    }

    @Override
    public Long setGame(User firstUser, User secondUser) {
        User firstUserWithGeneratedID = userService.addUser(firstUser);
        User secondUserWithGeneratedID = userService.addUser(secondUser);

        long idFirstPlayer = firstUserWithGeneratedID.getId();
        long idSecondPlayer = secondUserWithGeneratedID.getId();
        long idGame = gameStateService.generateGameId();

        List<User> users = Arrays.asList(firstUserWithGeneratedID, secondUserWithGeneratedID);
        GameState newGame = new GameState(idGame, users);
        gameStateService.setGame(idGame, idFirstPlayer, idSecondPlayer, newGame);
        return idGame;
    }
}
