package com.example.demo.models.dto;

public class Ball {
    private int chosenGear;
    private int chosenHoleInGear;

    public Ball(int newChosenGear, int newChosenHoleInGear) {
        chosenGear = newChosenGear;
        chosenHoleInGear = newChosenHoleInGear;
    }

    public Ball() {
        this(-1, -1);
    }

    public int getChosenGear() {
        return chosenGear;
    }

    public void setChosenGear(int chosenGear) {
        this.chosenGear = chosenGear;
    }

    public int getChosenHoleInGear() {
        return chosenHoleInGear;
    }

    public void setChosenHoleInGear(int chosenHoleInGear) {
        this.chosenHoleInGear = chosenHoleInGear;
    }
}
