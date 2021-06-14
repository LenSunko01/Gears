package ru.hse.gears.service.gamestate;

import ru.hse.gears.dao.allusers.AllUsersDao;
import ru.hse.gears.dao.gamestate.GameStateDao;
import ru.hse.gears.models.dto.Board;
import ru.hse.gears.models.dto.GameState;
import ru.hse.gears.models.dto.User;
import ru.hse.gears.web.controllers.UserController;
import ru.hse.gears.web.exceptions.AuthenticationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static ru.hse.gears.service.GameConstants.*;
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

    @Override
    public boolean validateToken(Long id, String token, GameState.CurrentPlayer player) {
        var game = gameStateRepository.getStateById(id);
        if (game == null) {
            throw new IllegalArgumentException("Game not found");
        }
        var users = game.getUsers();
        String correctToken;
        if (player.equals(GameState.CurrentPlayer.FIRSTPLAYER)) {
            correctToken = allUsers.getTokenByUsername(users.get(0).getUsername());
        } else {
            correctToken = allUsers.getTokenByUsername(users.get(1).getUsername());
        }
        return correctToken.equals(token);
    }

    @Override
    public GameState getStateById(Long id, String token, GameState.CurrentPlayer player) {
        if (!validateToken(id, token, player)) {
            throw new AuthenticationException();
        }
        return gameStateRepository.getStateById(id);
    }

    @Override
    public GameState updateStateById(
            Long id,
            String token,
            GameState newGameState,
            GameState.CurrentPlayer player
    ) {
        if (!validateToken(id, token, player)) {
            throw new AuthenticationException();
        }
        gameStateRepository.updateGameState(id, newGameState);
        return newGameState;
    }

    @Override
    public GameState updateBoardById(Long id, String token, GameState.CurrentPlayer player, Board board) {
        if (!validateToken(id, token, player)) {
            throw new AuthenticationException();
        }
        logger.info("Validated token, trying to update board");
        return gameStateRepository.updateBoardInGameState(id, player, board);
    }

    @Override
    public void deleteGameState(Long id, String token, GameState.CurrentPlayer player) {
        if (!validateToken(id, token, player)) {
            throw new AuthenticationException();
        }
        var gameState = gameStateRepository.getStateById(id);
        if (player.equals(GameState.CurrentPlayer.FIRSTPLAYER)) {
            gameState.setFirstPlayerHasEndedGame(true);
        } else {
            gameState.setSecondPlayerHasEndedGame(true);
        }
        if (gameState.isFirstPlayerHasEndedGame() && gameState.isSecondPlayerHasEndedGame()) {
            endGame(gameState);
            return;
        }
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
        logger.info("Game deleted");
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
