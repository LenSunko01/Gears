package com.example.demo.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    private boolean firstPlayerHasInitializedBoard;
    private boolean secondPlayerHasInitializedBoard;
    private boolean firstPlayerHasEndedGame;
    private boolean secondPlayerHasEndedGame;

    public GameState(List<User> users) {
        scoreOfFirstPlayer = 0L;
        scoreOfSecondPlayer = 0L;
        this.users = users;
        firstPlayerBoard = new Board();
        secondPlayerBoard = new Board();
        turn = new Turn();
        currentGameState = CurrentGameState.CONTINUE;
        firstPlayerHasInitializedBoard = false;
        secondPlayerHasInitializedBoard = false;
        firstPlayerHasEndedGame = false;
        secondPlayerHasEndedGame = false;
    }

    public GameState() {
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

    public boolean isFirstPlayerHasInitializedBoard() {
        return firstPlayerHasInitializedBoard;
    }

    public void setFirstPlayerHasInitializedBoard(boolean firstPlayerHasInitializedBoard) {
        this.firstPlayerHasInitializedBoard = firstPlayerHasInitializedBoard;
    }

    public boolean isSecondPlayerHasInitializedBoard() {
        return secondPlayerHasInitializedBoard;
    }

    public void setSecondPlayerHasInitializedBoard(boolean secondPlayerHasInitializedBoard) {
        this.secondPlayerHasInitializedBoard = secondPlayerHasInitializedBoard;
    }

    public boolean isFirstPlayerHasEndedGame() {
        return firstPlayerHasEndedGame;
    }

    public void setFirstPlayerHasEndedGame(boolean firstPlayerHasEndedGame) {
        this.firstPlayerHasEndedGame = firstPlayerHasEndedGame;
    }

    public boolean isSecondPlayerHasEndedGame() {
        return secondPlayerHasEndedGame;
    }

    public void setSecondPlayerHasEndedGame(boolean secondPlayerHasEndedGame) {
        this.secondPlayerHasEndedGame = secondPlayerHasEndedGame;
    }


    public enum CurrentPlayer {FIRSTPLAYER, SECONDPLAYER}

    public enum CurrentGameState {CONTINUE, DRAW, FIRSTPLAYER, SECONDPLAYER}

    protected class Turn {
        public Turn() {
            this.currentPlayer = CurrentPlayer.FIRSTPLAYER;
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

        public void addDegreeToArrayDegree(int degree) {
            this.degree.add(degree);
        }

        private int numberOfActiveGear = -1;
        private CurrentPlayer currentPlayer;

        public void setCurrentPlayer(CurrentPlayer player) { this.currentPlayer = player; }

        private ArrayList<Integer> degree;
    }


    public CurrentPlayer getCurrentPlayer() {
        return turn.currentPlayer;
    }

    public void setCurrentPlayer(CurrentPlayer currentPlayer) {
        turn.currentPlayer = currentPlayer;
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
