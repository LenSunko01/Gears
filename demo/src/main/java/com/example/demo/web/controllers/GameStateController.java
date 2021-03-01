package com.example.demo.web.controllers;

import com.example.demo.models.dto.GameState;
import com.example.demo.models.dto.User;
import com.example.demo.service.gamestate.GameStateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GameStateController {
    private GameStateService gameStateService;

    public GameStateController(GameStateService gameStateService) {
        this.gameStateService = gameStateService;
    }

    @GetMapping("/games")
    List<GameState> all() {
        return gameStateService.getAll();
    }

}
