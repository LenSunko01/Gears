package com.example.demo.service.gamestate;

import com.example.demo.dao.gamestate.GameStateDaoImpl;
import com.example.demo.models.dto.GameState;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameStateServiceImpl implements GameStateService {

    private final GameStateDaoImpl gameStateRepository;

    public GameStateServiceImpl(GameStateDaoImpl gameStateDao) {
        this.gameStateRepository = gameStateDao;
    }

    @Override
    public GameState getStateById(Long id) {
        return gameStateRepository.getStateById(id);
    }

    @Override
    public GameState getStateByUserId(Long id) {
        return gameStateRepository.getStateByUserId(id);
    }

    @Override
    public List<GameState> getAll() {
        return gameStateRepository.getAll();
    }

    @Override
    public GameState saveGameState(GameState game) {
        return gameStateRepository.saveGameState(game);
    }

    @Override
    public void addPlayersToGame(Long idGame, Long idUserOne, Long idUserSecond) {
        gameStateRepository.addPlayersToGame(idGame, idUserOne, idUserSecond);
    }

}
