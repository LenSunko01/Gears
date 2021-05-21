package com.example.demo;

import com.example.demo.models.dto.User;
import com.example.demo.service.login.LoginService;
import com.example.demo.service.registration.RegistrationService;
import com.example.demo.service.user.UserService;
import com.example.demo.web.controllers.UserController;
import org.junit.jupiter.api.Test;
import org.mockito.internal.stubbing.answers.AnswersWithDelay;
import org.mockito.internal.stubbing.answers.Returns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockAsyncContext;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.AsyncListener;
import java.util.AbstractMap;
import java.util.HashMap;

import static com.example.demo.web.controllers.ControllersConstants.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RegistrationService registrationService;

    @MockBean
    private LoginService loginService;

    @Test
    public void getUsersTest() throws Exception {
        var map = new HashMap<String, Long>();
        map.put("Jane", 100L);
        map.put("Bob", 15500L);
        when(userService.getAll()).thenReturn(map);
        var result = mockMvc.perform(get("/users"))
                .andExpect(request().asyncStarted()).andReturn();
        result.getAsyncResult();
        mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.Jane").value(100))
                .andExpect(jsonPath("$.Bob").value(15500L));
    }

    @Test
    public void getUsersEmptyMapTest() throws Exception {
        var map = new HashMap<String, Long>();
        when(userService.getAll()).thenReturn(map);
        var result = mockMvc.perform(get("/users"))
                .andExpect(request().asyncStarted()).andReturn();
        result.getAsyncResult();
        mockMvc.perform(asyncDispatch(result))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("{}"));
    }

    @Test
    public void getUsersTimeoutTest() throws Exception {
        var map = new HashMap<String, Long>();
        map.put("Jane", 100L);
        map.put("Bob", 15500L);
        doAnswer(new AnswersWithDelay(getUsersTimeoutInMilliseconds + 100L, new Returns(map))).when(userService).getAll();
        var result = mockMvc.perform(get("/users"))
                .andExpect(request().asyncStarted())
                .andReturn();
        // Trigger a timeout on the request
        MockAsyncContext ctx = (MockAsyncContext) result.getRequest().getAsyncContext();
        for (AsyncListener listener : ctx.getListeners()) {
            listener.onTimeout(null);
        }
        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isRequestTimeout()).andDo(print())
                .andExpect(jsonPath("$").value("Request timeout occurred."));
    }

    @Test
    public void getRandomUser() throws Exception {
        var mapEntry = new AbstractMap.SimpleEntry<>("Kate", 33L);
        when(userService.getRandomUser()).thenReturn(mapEntry);
        var result = mockMvc.perform(get("/random"))
                .andExpect(request().asyncStarted()).andReturn();
        result.getAsyncResult();
        mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.Kate").value(33));
    }

    @Test
    public void getRandomUserTimeoutTest() throws Exception {
        var mapEntry = new AbstractMap.SimpleEntry<>("Kate", 33L);
        doAnswer(new AnswersWithDelay(getRandomUserTimeoutInMilliseconds + 100L, new Returns(mapEntry))).when(userService).getRandomUser();
        var result = mockMvc.perform(get("/random"))
                .andExpect(request().asyncStarted())
                .andReturn();
        // Trigger a timeout on the request
        MockAsyncContext ctx = (MockAsyncContext) result.getRequest().getAsyncContext();
        for (AsyncListener listener : ctx.getListeners()) {
            listener.onTimeout(null);
        }
        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isRequestTimeout()).andDo(print())
                .andExpect(jsonPath("$").value("Request timeout occurred."));
    }

    @Test
    public void registerUserOkTest() throws Exception {
        when(registrationService.registerUser("Kate", "correctPassword1")).thenReturn(new User.UserInformation("token", 32L));
        var result = mockMvc.perform(post("/register").param("username", "Kate").param("password", "correctPassword1"))
                .andExpect(request().asyncStarted()).andReturn();
        result.getAsyncResult();
        mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"))
                .andExpect(jsonPath("$.id").value(32L));
    }

    @Test
    public void registerUserExceptionTest() throws Exception {
        given(registrationService.registerUser("Kate", "password")).willAnswer(invocation -> { throw new Exception("Exception");});
        var result = mockMvc.perform(post("/register").param("username", "Kate").param("password", "password"))
                .andExpect(request().asyncStarted()).andReturn();
        result.getAsyncResult();
        mockMvc.perform(asyncDispatch(result))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$").value("Exception"));
    }

    @Test
    public void registerUserTimeoutTest() throws Exception {
        doAnswer(new AnswersWithDelay(postRegisterUserTimeoutInMilliseconds + 100L,
                new Returns(new User.UserInformation("token", 1L))))
                .when(registrationService).registerUser("Kate", "pass");
        var result = mockMvc.perform(post("/register").param("username", "Kate").param("password", "pass"))
                .andExpect(request().asyncStarted()).andReturn();
        // Trigger a timeout on the request
        MockAsyncContext ctx = (MockAsyncContext) result.getRequest().getAsyncContext();
        for (AsyncListener listener : ctx.getListeners()) {
            listener.onTimeout(null);
        }
        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isRequestTimeout()).andDo(print())
                .andExpect(jsonPath("$").value("Request timeout occurred."));
    }

    @Test
    public void loginUserOkTest() throws Exception {
        when(loginService.loginUser("Kate", "correctPassword1")).thenReturn(new User.UserInformation("token", 32L));
        var result = mockMvc.perform(post("/login").param("username", "Kate").param("password", "correctPassword1"))
                .andExpect(request().asyncStarted()).andReturn();
        result.getAsyncResult();
        mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"))
                .andExpect(jsonPath("$.id").value(32L));
    }

    @Test
    public void loginUserExceptionTest() throws Exception {
        given(loginService.loginUser("Kate", "password")).willAnswer(invocation -> { throw new Exception("Exception");});
        var result = mockMvc.perform(post("/login").param("username", "Kate").param("password", "password"))
                .andExpect(request().asyncStarted()).andReturn();
        result.getAsyncResult();
        mockMvc.perform(asyncDispatch(result))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$").value("Exception"));
    }

    @Test
    public void loginUserTimeoutTest() throws Exception {
        doAnswer(new AnswersWithDelay(postLoginUserTimeoutInMilliseconds + 100L,
                new Returns(new User.UserInformation("token", 1L))))
                .when(loginService).loginUser("Kate", "pass");
        var result = mockMvc.perform(post("/login").param("username", "Kate").param("password", "pass"))
                .andExpect(request().asyncStarted()).andReturn();
        // Trigger a timeout on the request
        MockAsyncContext ctx = (MockAsyncContext) result.getRequest().getAsyncContext();
        for (AsyncListener listener : ctx.getListeners()) {
            listener.onTimeout(null);
        }
        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isRequestTimeout()).andDo(print())
                .andExpect(jsonPath("$").value("Request timeout occurred."));
    }

    @Test
    public void getUserByIdOkTest() throws Exception {
        var header = new HttpHeaders();
        header.add("token", "correctToken");
        when(userService.getUserById(2L, "correctToken")).thenReturn(new User(2L, "Katya", "123",32L));
        var result = mockMvc.perform(get("/user/{id}", 2L)
                .headers(header))
                .andExpect(request().asyncStarted()).andReturn();
        result.getAsyncResult();
        mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.username").value("Katya"))
                .andExpect(jsonPath("$.password").value("123"))
                .andExpect(jsonPath("$.points").value(32));
    }

    @Test
    public void getUserByIdExceptionTest() throws Exception {
        var header = new HttpHeaders();
        header.add("token", "wrongToken");
        given(userService.getUserById(2L, "wrongToken")).willAnswer(invocation -> { throw new Exception("Exception");});
        var result = mockMvc.perform(get("/user/{id}", 2L)
                .headers(header))
                .andExpect(request().asyncStarted()).andReturn();
        mockMvc.perform(asyncDispatch(result))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$").value("Exception"));
    }

    @Test
    public void getUserByIdTimeoutTest() throws Exception {
        var header = new HttpHeaders();
        header.add("token", "correctToken");
        doAnswer(new AnswersWithDelay(getUserTimeoutInMilliseconds + 100L,
                new Returns(new User(2L, "Katya", "123",32L))))
                .when(userService).getUserById(2L, "correctToken");
        var result = mockMvc.perform(get("/user/{id}", 2L)
                .headers(header))
                .andExpect(request().asyncStarted()).andReturn();
        // Trigger a timeout on the request
        MockAsyncContext ctx = (MockAsyncContext) result.getRequest().getAsyncContext();
        for (AsyncListener listener : ctx.getListeners()) {
            listener.onTimeout(null);
        }
        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isRequestTimeout()).andDo(print())
                .andExpect(jsonPath("$").value("Request timeout occurred."));
    }
}
