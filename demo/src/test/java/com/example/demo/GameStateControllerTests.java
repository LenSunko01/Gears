package com.example.demo;

import com.example.demo.models.dto.GameState;
import com.example.demo.models.dto.User;
import com.example.demo.service.gamestate.GameStateService;
import com.example.demo.service.login.LoginService;
import com.example.demo.service.registration.RegistrationService;
import com.example.demo.service.user.UserService;
import com.example.demo.web.controllers.GameStateController;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockAsyncContext;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.AsyncListener;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameStateController.class)
public class GameStateControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RegistrationService registrationService;

    @MockBean
    private LoginService loginService;

    @MockBean
    private GameStateService gameStateService;

    @Test
    public void getGameByIdOkTest() throws Exception {
        var headerKatya = new HttpHeaders();
        headerKatya.add("token", "correctTokenKatya");
        var headerMaks = new HttpHeaders();
        headerMaks.add("token", "correctTokenMaks");
        var userKatya = new User(0L, "Katya", "123",32L, 0L, 0L, 0L);
        var userMaks = new User(1L, "Maks", "123",32L, 0L, 0L, 0L);
        var list = new ArrayList<User>();
        list.add(userKatya);
        list.add(userMaks);
        var gameState = new GameState(list);
        gameState.setFirstPlayerHasInitializedBoard(true);
        gameState.setSecondPlayerHasInitializedBoard(true);
        gameState.setCurrentPlayer(GameState.CurrentPlayer.FIRSTPLAYER);

        when(gameStateService.getStateById(anyLong(), anyString())).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                return gameState;
            }
        });

        var result = mockMvc.perform(get("/game/{id}/player/{currentPlayer}",
                2L, String.valueOf(GameState.CurrentPlayer.FIRSTPLAYER))
                .headers(headerKatya))
                .andExpect(request().asyncStarted()).andReturn();
        result.getAsyncResult();
        mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk());

        result = mockMvc.perform(get("/game/{id}/player/{currentPlayer}",
                2L, String.valueOf(GameState.CurrentPlayer.SECONDPLAYER))
                .headers(headerMaks))
                .andExpect(request().asyncStarted()).andReturn();
        // Trigger a timeout on the request
        MockAsyncContext ctx = (MockAsyncContext) result.getRequest().getAsyncContext();
        for (AsyncListener listener : ctx.getListeners()) {
            listener.onTimeout(null);
        }
        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isRequestTimeout()).andDo(print())
                .andExpect(jsonPath("$").value("Request timeout occurred."));

        gameState.setCurrentPlayer(GameState.CurrentPlayer.SECONDPLAYER);
        result = mockMvc.perform(get("/game/{id}/player/{currentPlayer}",
                2L, String.valueOf(GameState.CurrentPlayer.SECONDPLAYER))
                .headers(headerMaks))
                .andExpect(request().asyncStarted()).andReturn();

        result.getAsyncResult();
        mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk());
    }
}
