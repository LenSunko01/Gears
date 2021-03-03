package com.example.demo.service.gamestate;

import com.example.demo.dao.gamestate.GameStateDaoImpl;
import com.example.demo.models.dto.GameState;
import com.example.demo.models.dto.User;
import com.example.demo.service.user.UserService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GameStateServiceImpl implements GameStateService {

    private final GameStateDaoImpl gameStateHolder;

    public GameStateServiceImpl(GameStateDaoImpl gameStateDao) {
        this.gameStateHolder = gameStateDao;
    }

    @Override
    public GameState getStateById(Long id) {
        return gameStateHolder.getStateById(id);
    }

    @Override
    public GameState getStateByUserId(Long id) {
        return gameStateHolder.getStateByUserId(id);
    }

    @Override
    public List<GameState> getAll() {
        return gameStateHolder.getAll();
    }

    @Override
    public Long generateGameId() {
        return gameStateHolder.generateGameId();
    }

    @Override
    public void setGame(Long idGame, Long idUserOne, Long idUserSecond, GameState game) {
        gameStateHolder.setGame(idGame, idUserOne, idUserSecond, game);
    }

}
