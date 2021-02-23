package com.example.demo;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
class User {

    private @Id @GeneratedValue Long id;
    private String name;
    private Long points;

    User() {}

    User(String name, Long points) {
        this.name = name;
        this.points = points;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Long getPoints() {
        return this.points;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
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
        return Objects.equals(this.id, user.id) && Objects.equals(this.name, user.name)
                && Objects.equals(this.points, user.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name, this.points);
    }

    @Override
    public String toString() {
        return "Employee{" + "id=" + this.id +
                ", name='" + this.name + '\'' +
                ", points='" + this.points + '\'' + '}';
    }
}