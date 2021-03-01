package com.example.demo.dao.gamestate;

import java.util.List;

public class GameStateRecord {
    private List<Long> userIds;
    Long id;

    public GameStateRecord(List<Long> userId, Long id) {
        this.userIds = userId;
        this.id = id;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
