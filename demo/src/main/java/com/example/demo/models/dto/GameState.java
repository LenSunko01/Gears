package com.example.demo.models.dto;

import java.util.List;

public class GameState {
    private Long id;   // think out how to generate :)
    private List<User> users;

    public GameState(List<User> users) {
        this.users = users;
    }

    public GameState(Long id, List<User> users) {
        this.id = id;
        this.users = users;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
