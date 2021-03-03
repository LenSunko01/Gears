package com.example.demo.models.dto;

import java.util.List;

public class GameState {
    private Long id;   // think out how to generate :)
    int numberOfActiveGear;
    int scoreOfFirstPlayer;
    int scoreOfSecondPlayer;


    public GameState(Long id, List<User> users) {
        this.id = id;
        scoreOfFirstPlayer = 0;
        scoreOfSecondPlayer = 0;
        numberOfActiveGear = (int) ((Math.random() * (5)));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNumberOfActiveGear(int numberOfActiveGear) {
        this.numberOfActiveGear = numberOfActiveGear;
    }

    public void setScoreOfFirstPlayer(int scoreOfFirstPlayer) {
        this.scoreOfFirstPlayer = scoreOfFirstPlayer;
    }

    public void setScoreOfSecondPlayer(int scoreOfSecondPlayer) {
        this.scoreOfSecondPlayer = scoreOfSecondPlayer;
    }


    public int getNumberOfActiveGear() {
        return numberOfActiveGear;
    }

    public int getScoreOfFirstPlayer() {
        return scoreOfFirstPlayer;
    }

    public int getScoreOfSecondPlayer() {
        return scoreOfSecondPlayer;
    }


}
