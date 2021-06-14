package ru.hse.gears.models.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Gear {
    private int degree;
    private boolean isLast;
    private boolean isFirst;
    List<Hole> holes = new ArrayList<>();
    private int numberOfHoles;


    public Gear() {}

    public Gear(Gear other) {
        this.isLast = other.isLast;
        this.isFirst = other.isFirst;
        this.radius = other.radius;
        this.degree = other.degree;
        this.numberOfHoles = other.numberOfHoles;
        this.downNeighbours = other.downNeighbours;
        this.upperNeighbours = other.upperNeighbours;
        this.x = other.x;
        this.y = other.y;
        List<Hole> newHoles = new ArrayList<>();
        for (Hole hole: other.holes) {
            newHoles.add(new Hole(hole));
        }
        this.holes = newHoles;

    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getRadius() {
        return radius;
    }
    public void setXY(int x, int y) {
        this.x = x + radius / 2;
        this.y = y + radius / 2;
    }

    private  float x;
    private  float y;
    private int radius;
    public List<Integer> getDownNeighbours() {
        return downNeighbours;
    }

    public void setDownNeighbours(ArrayList<Integer> downNeighbours) {
        this.downNeighbours = downNeighbours;
    }

    public List<Integer> getUpperNeighbours() {
        return upperNeighbours;
    }

    public void setUpperNeighbours(ArrayList<Integer> upperNeighbours) {
        this.upperNeighbours = upperNeighbours;
    }

    List<Integer> downNeighbours;
    List<Integer> upperNeighbours;
    public Gear(int numberOfHoles, boolean isLast, boolean isFirst, float x, float y, int radius,
                List<Integer> downNeighbours, List<Integer> upperNeighbours) {
        this.numberOfHoles = numberOfHoles;
        this.isLast = isLast;
        this.isFirst = isFirst;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.downNeighbours = downNeighbours;
        this.upperNeighbours = upperNeighbours;
        int currentDegreeToAdd = 0;
        int step = 360 / numberOfHoles;
        for (int i = 0; i < numberOfHoles; i++) {
            Hole bufferHole = new Hole(i);
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
        this.degree += (degree + 360) % 360;
        this.degree %= 360;
        for (Hole hole : holes) {
            hole.setDegree((hole.getDegree() + degree) % 360);
        }
    }

    public List<Hole> getHoles() {
        return holes;
    }

    @JsonProperty(value="isLast")
    public boolean isLast() {
        return isLast;
    }

    @JsonProperty(value="isLast")
    public void setIsLast(boolean val) {
        isLast = val;
    }

    @JsonProperty(value="isFirst")
    public boolean isFirst() {
        return isFirst;
    }

    @JsonProperty(value="isFirst")
    public void setIsFirst(boolean val) {
        isFirst = val;
    }

    public static class Hole {
        private int capacity;
        private int degree;
        private boolean isFree = true;
        private int numberOfBall;
        private int numberOfHole;

        public Hole(int numberOfHole) {
            capacity = 1;
            degree = 0;
            this.numberOfHole = numberOfHole;
        }

        public Hole() { }
        public Hole(Hole other) {
            this.capacity = other.capacity;
            this.degree = other.degree;
            this.isFree = other.isFree;
            this.numberOfBall = other.numberOfBall;
            this.numberOfHole = other.numberOfHole;
        }

        public int getCapacity() {
            return capacity;
        }

        public int getDegree() {
            return degree;
        }

        public void setDegree(int degree) {
            this.degree = (360 + degree) % 360;
        }

        @JsonProperty(value="isFree")
        public boolean isFree() {
            return isFree;
        }

        @JsonProperty(value="isFree")
        public void setFree(boolean free) {
            isFree = free;
        }

        public int getNumberOfBall() {
            return numberOfBall;
        }

        public void setNumberOfBall(int numberOfBall) {
            this.numberOfBall = numberOfBall;
        }

        public int getNumberOfHole() {
            return numberOfHole;
        }
    }
}