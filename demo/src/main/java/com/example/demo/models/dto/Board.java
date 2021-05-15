package com.example.demo.models.dto;

import java.util.ArrayList;

public class Board {
    private ArrayList<Gear> gears;
    private ArrayList<Ball> balls = new ArrayList<>();
    private Gutter rightGutter = new Gutter(60);
    private Gutter leftGutter = new Gutter(300);
    private Pot pot = new Pot();
    final private int step = 10;

    public Gutter getLeftGutter() {
        return leftGutter;
    }

    public void setLeftGutter(Gutter leftGutter) {
        this.leftGutter = leftGutter;
    }

    public Gutter getRightGutter() {
        return rightGutter;
    }

    public void setRightGutter(Gutter rightGutter) {
        this.rightGutter = rightGutter;
    }

    public Pot getPot() {
        return pot;
    }

    public void setPot(Pot pot) {
        this.pot = pot;
    }

    public ArrayList<Ball> getBalls() {
        return balls;
    }

    public void setBalls(ArrayList<Ball> balls) {
        this.balls = balls;
    }

    public ArrayList<Gear> getGears() {
        return gears;
    }

    public void setGears(ArrayList<Gear> gears) {
        this.gears = gears;
    }

    public void rebuild(int degree, int activeGear) {
        for (int i = 0; i < Math.abs(degree) / step; i++) {
            Gear changingGear = getGears().get(activeGear);
            if (degree >= 0) {
                changingGear.setDegree(step);
            } else {
                changingGear.setDegree(360 - step);
            }
            int indexOfDownNeighbour;
            indexOfDownNeighbour = getDownNeighbourIndex(changingGear, changingGear.isLast());

            int indexOfUpperNeighbour;
            indexOfUpperNeighbour = getIndexUpperNeighbour(changingGear);

            Gear downNeighbourOfChangingGear = null;
            Gear upperNeighbourOfChangingGear = null;

            if (indexOfDownNeighbour != -1) {
                downNeighbourOfChangingGear = this.getGears().get(indexOfDownNeighbour);
            }
            if (indexOfUpperNeighbour != -1) {
                upperNeighbourOfChangingGear = this.getGears().get(indexOfUpperNeighbour);
            }

            extractBallsFromLastGear(activeGear, changingGear);

            putBallsInFirstGear(activeGear, changingGear);

            putToDownNeighbour(changingGear, indexOfDownNeighbour, downNeighbourOfChangingGear);

            getFromUpperNeighbour(activeGear, changingGear, upperNeighbourOfChangingGear);

            var arrayOfGears = this.getGears();
            arrayOfGears.set(activeGear, changingGear);
            if (indexOfUpperNeighbour != -1) {
                arrayOfGears.set(indexOfUpperNeighbour, upperNeighbourOfChangingGear);
            }
            if (indexOfDownNeighbour != -1) {
                arrayOfGears.set(indexOfDownNeighbour, downNeighbourOfChangingGear);
            }
        }

    }

    private void getFromUpperNeighbour(int activeGear, Gear changingGear, Gear upperNeighbourOfChangingGear) {
        if (upperNeighbourOfChangingGear != null) {
            for (Gear.Hole holeOfChangingGear : changingGear.getHoles()) {
                for (Gear.Hole holeOfUpperNeighbourOfChangingGear : upperNeighbourOfChangingGear.getHoles()) {
                    if (holeOfChangingGear.getDegree() % 180 == 0) {
                        continue;
                    }
                    if (holeOfChangingGear.getDegree() % 180 == (180 - holeOfUpperNeighbourOfChangingGear.getDegree()) % 180) {
                        holeOfChangingGear.setFree(false);
                        holeOfUpperNeighbourOfChangingGear.setFree(true);
                        moveBall(activeGear, holeOfUpperNeighbourOfChangingGear, holeOfChangingGear.getNumberOfHole());
                        changingGear.getHoles().set(holeOfChangingGear.getNumberOfHole(), holeOfChangingGear);
                        upperNeighbourOfChangingGear.getHoles().set(holeOfUpperNeighbourOfChangingGear.getNumberOfHole(), holeOfUpperNeighbourOfChangingGear);
                    }
                }
            }
        }
    }

    private void putToDownNeighbour(Gear changingGear, int indexOfDownNeighbour, Gear downNeighbourOfChangingGear) {
        if (downNeighbourOfChangingGear != null) {
            for (Gear.Hole holeOfChangingGear : changingGear.getHoles()) {
                for (Gear.Hole holeOfDownNeighbourOfChangingGear : downNeighbourOfChangingGear.getHoles()) {
                    if (holeOfChangingGear.getDegree() % 180 == 0) {
                        continue;
                    }
                    if (holeOfChangingGear.getDegree() % 180 == (180 - holeOfDownNeighbourOfChangingGear.getDegree()) % 180) {
                        holeOfChangingGear.setFree(true);
                        holeOfDownNeighbourOfChangingGear.setFree(false);
                        moveBall(indexOfDownNeighbour, holeOfChangingGear, holeOfDownNeighbourOfChangingGear.getNumberOfHole());
                        changingGear.getHoles().set(holeOfChangingGear.getNumberOfHole(), holeOfChangingGear);
                        downNeighbourOfChangingGear.getHoles().set(holeOfDownNeighbourOfChangingGear.getNumberOfHole(), holeOfDownNeighbourOfChangingGear);
                    }
                }
            }
        }
    }

    private void putBallsInFirstGear(int activeGear, Gear changingGear) {
        if (changingGear.isFirst()) {
            for (Gear.Hole holeOfChangingGear : changingGear.getHoles()) {
                if (holeOfChangingGear.getDegree() == this.getLeftGutter().getDegree() ||
                        holeOfChangingGear.getDegree() == this.getRightGutter().getDegree()) {

                    if (holeOfChangingGear.getDegree() == this.getLeftGutter().getDegree()) {
                        this.getLeftGutter().setHowManyBalls(getLeftGutter().getHowManyBalls() - holeOfChangingGear.getCapacity());
                    }

                    if (holeOfChangingGear.getDegree() == this.getRightGutter().getDegree()) {
                        this.getRightGutter().setHowManyBalls(getRightGutter().getHowManyBalls() - holeOfChangingGear.getCapacity());
                    }
                    holeOfChangingGear.setFree(false);
                    moveBall(activeGear, holeOfChangingGear, holeOfChangingGear.getNumberOfHole());
                    changingGear.getHoles().set(holeOfChangingGear.getNumberOfHole(), holeOfChangingGear);
                }
            }
            var arrayOfGears = this.getGears();
            arrayOfGears.set(activeGear, changingGear);
        }
    }

    private void moveBall(int activeGear, Gear.Hole holeOfChangingGear, int numberOfHole) {
        int numberOfBallInHole = holeOfChangingGear.getNumberOfBall();
        Ball fallenBall = this.getBalls().get(numberOfBallInHole);
        fallenBall.setChosenGear(activeGear);
        fallenBall.setChosenHoleInGear(numberOfHole);
        this.getBalls().set(numberOfBallInHole, fallenBall);
    }

    private void extractBallsFromLastGear(int activeGear, Gear changingGear) {
        if (changingGear.isLast()) {
            for (Gear.Hole holeOfChangingGear : changingGear.getHoles()) {
                if (holeOfChangingGear.getDegree() == this.getPot().getDegree() && !holeOfChangingGear.isFree()) {
                    this.getPot().setHowManyBalls(getPot().getHowManyBalls() + holeOfChangingGear.getCapacity());
                    holeOfChangingGear.setFree(true);

                    int numberOfBallInHole = holeOfChangingGear.getNumberOfBall();
                    Ball fallenBall = this.getBalls().get(numberOfBallInHole);
                    this.getBalls().remove(fallenBall);
                    changingGear.getHoles().set(holeOfChangingGear.getNumberOfHole(), holeOfChangingGear);
                }
            }
            var arrayOfGears = this.getGears();
            arrayOfGears.set(activeGear, changingGear);
        }
    }

    private int getIndexUpperNeighbour(Gear changingGear) {
        if (changingGear.isFirst()) {
            return -1;
        } else {
            return changingGear.getNeighbours().get(0);
        }
    }

    private int getDownNeighbourIndex(Gear changingGear, boolean last) {
        if (last) {
            return -1;
        } else {
            return changingGear.getNeighbours().get(1);
        }
    }


    public int getStep() {
        return step;
    }

    private class Gutter {
        public Gutter() {
        }

        private int degree = 60;
        private int howManyBalls = 6;

        public Gutter(int degree) {
            this.degree = degree;
            for (int i = 0; i < howManyBalls; i++) {
                balls.add(new Ball());
            }
        }

        public int getDegree() {
            return degree;
        }

        public void setDegree(int degree) {
            this.degree = degree;
        }

        public int getHowManyBalls() {
            return howManyBalls;
        }

        public void setHowManyBalls(int howManyBalls) {
            this.howManyBalls = howManyBalls;
        }
    }

    private class Pot {
        public Pot() {
        }

        private int degree = 120;
        private int howManyBalls = 0;

        public int getDegree() {
            return degree;
        }

        public void setDegree(int degree) {
            this.degree = degree;
        }

        public int getHowManyBalls() {
            return howManyBalls;
        }

        public void setHowManyBalls(int howManyBalls) {
            this.howManyBalls = howManyBalls;
        }
    }
}
