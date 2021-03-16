//package com.example.demo.web.controllers;
//
//import com.example.demo.models.dto.GameState;
//import com.example.demo.service.gamestate.GameStateService;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.context.request.async.DeferredResult;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.ForkJoinPool;
//
//@RestController
//public class GameStateController {
//    private static final Log logger = LogFactory.getLog(UserController.class);
//    private final GameStateService gameStateService;
//
//    public GameStateController(GameStateService gameStateService) {
//        this.gameStateService = gameStateService;
//    }
//
//    @GetMapping("/games")
//    DeferredResult<List<GameState>> all() {
//        logger.info("Received get all game states request");
//        DeferredResult<List<GameState>> output = new DeferredResult<>(5L, Collections.emptyList());
//
//        ForkJoinPool.commonPool().submit(() -> {
//            logger.info("Processing in separate thread");
//            var result = gameStateService.getAll();
//            output.setResult(result);
//        });
//
//        logger.info("Thread freed");
//        return output;
//    }
//
//    @GetMapping("/game/{id}")
//    DeferredResult<GameState> gameState(@PathVariable Long id) {
//        logger.info("Received get game state bu ID request");
//        DeferredResult<GameState> output = new DeferredResult<>(5L, IllegalStateException::new);
//
//        ForkJoinPool.commonPool().submit(() -> {
//            logger.info("Processing in separate thread");
//            var result = gameStateService.getStateById(id);
//            output.setResult(result);
//        });
//
//        logger.info("Thread freed");
//        return output;
//    }
//}
