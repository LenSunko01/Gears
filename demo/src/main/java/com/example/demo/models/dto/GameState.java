package com.example.demo.models.dto;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private Long id;
    private Long scoreOfFirstPlayer;
    private Long scoreOfSecondPlayer;
    private Board firstPlayerBoard;
    private Board secondPlayerBoard;
    private List<User> users;
    private Turn turn;
    private CurrentGameState currentGameState;
    public GameState(Long id, Long scoreOfFirstPlayer, Long scoreOfSecondPlayer) {
        this.id = id;
        this.scoreOfFirstPlayer = scoreOfFirstPlayer;
        this.scoreOfSecondPlayer = scoreOfSecondPlayer;
    }
    public Board getFirstPlayerBoard() {
        return firstPlayerBoard;
    }

    public void setFirstPlayerBoard(Board firstPlayerBoard) {
        this.firstPlayerBoard = firstPlayerBoard;
    }

    public Board getSecondPlayerBoard() {
        return secondPlayerBoard;
    }

    public void setSecondPlayerBoard(Board secondPlayerBoard) {
        this.secondPlayerBoard = secondPlayerBoard;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public CurrentGameState getCurrentGameState() {
        return currentGameState;
    }

    public void setCurrentGameState(CurrentGameState currentGameState) {
        this.currentGameState = currentGameState;
    }

    public Turn getTurn() {
        return turn;
    }

    public void setTurn(Turn turn) {
        this.turn = turn;
    }

    public enum CurrentPlayer {FIRSTPLAYER, SECONDPLAYER}
    public enum CurrentGameState {CONTINUE, DRAW, FIRSTPLAYER, SECONDPLAYER}

    private class Turn {
        private Turn() {
            int generateTurn = (int) ((Math.random() * (2)));
            this.currentPlayer = CurrentPlayer.values()[generateTurn];
            this.degree = null;
        }

        public int getNumberOfActiveGear() {
            return numberOfActiveGear;
        }

        public void setNumberOfActiveGear(int numberOfActiveGear) {
            this.numberOfActiveGear = numberOfActiveGear;
        }

        public ArrayList<Integer> getDegree() {
            return degree;
        }

        public void setDegree(ArrayList<Integer> degree) {
            this.degree = degree;
        }

        private int numberOfActiveGear = -1;
        private CurrentPlayer currentPlayer;
        private ArrayList<Integer> degree;
    }

    public GameState(List<User> users) {
        scoreOfFirstPlayer = 0L;
        scoreOfSecondPlayer = 0L;
        this.users = users;
        firstPlayerBoard = new Board();
        secondPlayerBoard = new Board();
        turn = new Turn();
        currentGameState = CurrentGameState.CONTINUE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setScoreOfFirstPlayer(Long scoreOfFirstPlayer) {
        this.scoreOfFirstPlayer = scoreOfFirstPlayer;
    }

    public void setScoreOfSecondPlayer(Long scoreOfSecondPlayer) {
        this.scoreOfSecondPlayer = scoreOfSecondPlayer;
    }


    public Long getScoreOfFirstPlayer() {
        return scoreOfFirstPlayer;
    }

    public Long getScoreOfSecondPlayer() {
        return scoreOfSecondPlayer;
    }

}
