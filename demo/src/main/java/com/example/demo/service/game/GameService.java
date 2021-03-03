package com.example.demo.service.game;

import com.example.demo.models.dto.User;


public interface GameService {
    Long setGame(User firstUser, User secondUser);
}
