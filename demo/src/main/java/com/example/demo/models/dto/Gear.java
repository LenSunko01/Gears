package com.example.demo.models.dto;

import java.util.ArrayList;

public class Gear {
    private int degree;
    private final boolean isLast;
    ArrayList<Hole> holes = new ArrayList<>();
    private final int numberOfHoles;

    public Gear(int numberOfHoles, boolean isLast) {
        this.numberOfHoles = numberOfHoles;
        this.isLast = isLast;
        int currentDegreeToAdd = 0;
        int step = 360 / numberOfHoles;
        for (int i = 0; i < numberOfHoles; i++) {
            Hole bufferHole = new Hole();
            bufferHole.setDegree(currentDegreeToAdd);
            holes.add(i, bufferHole);
            currentDegreeToAdd += step;
        }
    }

    public int getNumberOfHoles() {
        return numberOfHoles;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree += degree % 360;
        for (Hole hole : holes) {
            hole.setDegree((hole.getDegree() + degree) % 360);
        }
    }

    public ArrayList<Hole> getHoles() {
        return holes;
    }

    protected class Hole {
        private final int capacity;
        private int degree;

        public Hole() {
            capacity = 1;
            degree = 0;
        }

        public int getCapacity() {
            return capacity;
        }

        public int getDegree() {
            return degree;
        }

        public void setDegree(int degree) {
            this.degree = degree % 360;
        }
    }
}