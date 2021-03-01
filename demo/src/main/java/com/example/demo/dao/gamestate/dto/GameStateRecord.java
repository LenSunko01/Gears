package com.example.demo.dao.gamestate.dto;

import java.util.List;

public class GameStateRecord {
    private Long id;
    private List<Long> userIds;

    public GameStateRecord(List<Long> userIds) {
        this.userIds = userIds;
    }

    public GameStateRecord(Long id, List<Long> userIds) {
        this.id = id;
        this.userIds = userIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
}
