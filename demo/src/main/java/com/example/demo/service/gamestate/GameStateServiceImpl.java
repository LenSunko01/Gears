package com.example.demo.service.gamestate;

import com.example.demo.dao.allusers.AllUsersDao;
import com.example.demo.dao.gamestate.GameStateDao;
import com.example.demo.models.dto.Board;
import com.example.demo.models.dto.GameState;
import com.example.demo.models.dto.User;
import com.example.demo.service.user.UserService;
import com.example.demo.web.controllers.UserController;
import com.example.demo.web.exceptions.AuthenticationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.example.demo.service.GameConstants.*;
import static java.lang.Long.max;
import static java.lang.Long.min;

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
    @Override
    public void validateToken(Long id, String token) {
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
    public GameState updateBoardById(Long id, String token, GameState.CurrentPlayer player, Board board) {
        validateToken(id, token);
        logger.info("Validated token, trying to update board");
        return gameStateRepository.updateBoardInGameState(id, player, board);
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
    public boolean checkGameExists(Long id) {
        return gameStateRepository.checkGameExists(id);
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
        var currentGameState = gameState.getCurrentGameState();
        gameStateRepository.deleteGame(gameState.getId());
        allUsers.updateTotalGamesById(firstUser.getId(), firstUser.getTotalNumberOfGames() + 1);
        allUsers.updateTotalGamesById(secondUser.getId(), secondUser.getTotalNumberOfGames() + 1);
        if (currentGameState == GameState.CurrentGameState.DRAW) {
            return;
        }
        User winner;
        User loser;
        if (gameState.getCurrentGameState() == GameState.CurrentGameState.FIRSTPLAYER) {
            winner = firstUser;
            loser = secondUser;
        } else {
            winner = firstUser;
            loser = secondUser;
        }
        allUsers.updateGamesWonById(winner.getId(), winner.getNumberOfGamesWon() + 1);
        allUsers.updateGamesLostById(loser.getId(), loser.getNumberOfGamesLost() + 1);
        var winnerPoints = winner.getPoints();
        var loserPoints = loser.getPoints();
        long pointsDifference;
        if (winnerPoints >= loserPoints) {
            if (loserPoints == 0) {
                allUsers.updatePointsById(winner.getId(), winnerPoints + pointsIfStrongerBeatsWeaker);
                return;
            }
            pointsDifference = min(loserPoints, pointsIfStrongerBeatsWeaker);
        } else {
            pointsDifference = max(min(loserPoints, pointsIfWeakerBeatsStronger),
                    (loserPoints - winnerPoints) / pointsNormalizationFactor);
        }
        allUsers.updatePointsById(winner.getId(), winnerPoints + pointsDifference);
        allUsers.updatePointsById(loser.getId(), loserPoints - pointsDifference);
    }
}
