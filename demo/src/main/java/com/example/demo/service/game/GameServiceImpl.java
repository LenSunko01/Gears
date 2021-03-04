package com.example.demo.service.game;

import com.example.demo.models.dto.GameState;
import com.example.demo.models.dto.User;
import com.example.demo.service.gamestate.GameStateService;
import com.example.demo.service.user.UserService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
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
        List<User> users = Arrays.asList(firstUserWithGeneratedID, secondUserWithGeneratedID);
        GameState bufferGame = new GameState(users);

        GameState newGame = gameStateService.saveGameState(bufferGame);
        long idGame = newGame.getId();
        gameStateService.addPlayersToGame(idGame, idFirstPlayer, idSecondPlayer);
        return idGame;
    }
}
