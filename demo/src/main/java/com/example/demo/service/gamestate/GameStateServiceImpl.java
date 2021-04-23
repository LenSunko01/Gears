package com.example.demo.service.gamestate;

import com.example.demo.dao.allusers.AllUsersDao;
import com.example.demo.dao.gamestate.GameStateDao;
import com.example.demo.models.dto.GameState;
import com.example.demo.models.dto.User;
import com.example.demo.service.user.UserService;
import com.example.demo.web.controllers.UserController;
import com.example.demo.web.exceptions.AuthenticationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class GameStateServiceImpl implements GameStateService {
    private static final Log logger = LogFactory.getLog(UserController.class);
    private final GameStateDao gameStateRepository;
    private final AllUsersDao allUsers;
    public GameStateServiceImpl(
            GameStateDao gameStateDao,
            AllUsersDao allUsers) {
        this.gameStateRepository = gameStateDao;
        this.allUsers = allUsers;
    }

    /* accepts game ID and throws exception if token matches none of the users in the game */
    private void validateToken(Long id, String token) {
        var game = gameStateRepository.getStateById(id);
        var users = game.getUsers();
        var correctTokenFirstUser = allUsers.getTokenByUsername(users.get(0).getUsername());
        var correctTokenSecondUser = allUsers.getTokenByUsername(users.get(1).getUsername());
        if (!correctTokenFirstUser.equals(token) && !correctTokenSecondUser.equals(token)) {
            throw new AuthenticationException();
        }
    }

    @Override
    public GameState getStateById(Long id, String token) {
        validateToken(id, token);
        return gameStateRepository.getStateById(id);
    }

    @Override
    public GameState updateStateById(Long id, String token, GameState newGameState) {
        validateToken(id, token);
        logger.info("Validated token, trying to update game state");
        gameStateRepository.updateGameState(id, newGameState);
        return newGameState;
    }

    @Override
    public void deleteGameState(Long id, String token) {
        validateToken(id, token);
        var gameState = gameStateRepository.getStateById(id);
        var count = gameState.getCountPlayersLeftGame();
        if (count == 1) {
            endGame(gameState);
            return;
        }
        gameState.setCountPlayersLeftGame(1);
        gameStateRepository.updateGameState(id, gameState);
    }

    @Override
    public Long setGame(User firstUser, User secondUser) {
        GameState game = new GameState(Arrays.asList(firstUser, secondUser));
        return gameStateRepository.saveGameState(game).getId();
    }

    private void endGame(GameState gameState) {
        var buffer = gameState.getUsers();
        var firstUser = buffer.get(0);
        var secondUser = buffer.get(1);
        if (gameState.getCurrentGameState() == GameState.CurrentGameState.FIRSTPLAYER) {
            firstUser.setPoints(firstUser.getPoints() + gameState.getScoreOfFirstPlayer());
            return;
        }
        if (gameState.getCurrentGameState() == GameState.CurrentGameState.SECONDPLAYER) {
            secondUser.setPoints(secondUser.getPoints() + gameState.getScoreOfSecondPlayer());
            return;
        }
        firstUser.setPoints((firstUser.getPoints() + gameState.getScoreOfFirstPlayer()) / 2);
        secondUser.setPoints((secondUser.getPoints() + gameState.getScoreOfSecondPlayer()) / 2);
        gameStateRepository.deleteGame(gameState.getId());
    }
}
