package com.example.demo.models.dto;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class User {
    public static class UserInformation {
        public String token;
        public Long id;
        public UserInformation(String token, Long id) {
            this.token = token;
            this.id = id;
        }
    }

    private Long id;
    private String username;
    private String password;
    private Long points;

    public User() {}

    public User(Long id, String username, String password, Long points) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.points = points;
    }

    public User(User user) {
        this.id = user.id;
        this.username = user.username;
        this.password = user.password;
        this.points = user.points;
    }

    public Long getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public Long getPoints() {
        return this.points;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof User))
            return false;
        User user = (User) o;
        return Objects.equals(this.id, user.id) && Objects.equals(this.username, user.username)
                && Objects.equals(this.password, user.password)
                && Objects.equals(this.points, user.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.username,
                this.password, this.points);
    }

    @Override
    public String toString() {
        return "Employee{" + "id=" + this.id +
                ", username='" + this.username + '\'' +
                ", password='" + this.password + '\'' +
                ", points='" + this.points + '\'' + '}';
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}