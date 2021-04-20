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
            if (changingGear.isLast()) {
                indexOfDownNeighbour = -1;
            } else {
                indexOfDownNeighbour = changingGear.getNeighbours().get(1);
            }

            int indexOfUpperNeighbour;
            if (changingGear.isFirst()) {
                indexOfUpperNeighbour = -1;
            } else {
                indexOfUpperNeighbour = changingGear.getNeighbours().get(0);
            }

            Gear downNeighbourOfChangingGear = null;
            Gear upperNeighbourOfChangingGear = null;
            if (indexOfDownNeighbour != -1) {
                downNeighbourOfChangingGear = this.getGears().get(indexOfDownNeighbour);
            }
            if (indexOfUpperNeighbour != -1) {
                upperNeighbourOfChangingGear = this.getGears().get(indexOfUpperNeighbour);
            }

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
                        int numberOfBallInHole = holeOfChangingGear.getNumberOfBall();
                        Ball fallenBall = this.getBalls().get(numberOfBallInHole);
                        fallenBall.setChosenGear(activeGear);
                        fallenBall.setChosenHoleInGear(holeOfChangingGear.getNumberOfHole());
                        this.getBalls().set(numberOfBallInHole, fallenBall);
                        changingGear.getHoles().set(holeOfChangingGear.getNumberOfHole(), holeOfChangingGear);
                    }
                }
                var arrayOfGears = this.getGears();
                arrayOfGears.set(activeGear, changingGear);
            }


            for (Gear.Hole holeOfChangingGear : changingGear.getHoles()) {
                if (downNeighbourOfChangingGear != null) {
                    for (Gear.Hole holeOfDownNeighbourOfChangingGear : downNeighbourOfChangingGear.getHoles()) {
                        if (holeOfChangingGear.getDegree() % 180 == 0) {
                            continue;
                        }
                        if (holeOfChangingGear.getDegree() % 180 == (180 - holeOfDownNeighbourOfChangingGear.getDegree()) % 180) {
                            holeOfChangingGear.setFree(true);
                            holeOfDownNeighbourOfChangingGear.setFree(false);
                            int numberOfBallInHole = holeOfChangingGear.getNumberOfBall();
                            Ball fallenBall = this.getBalls().get(numberOfBallInHole);
                            fallenBall.setChosenGear(indexOfDownNeighbour);
                            fallenBall.setChosenHoleInGear(holeOfDownNeighbourOfChangingGear.getNumberOfHole());
                            this.getBalls().set(numberOfBallInHole, fallenBall);
                            changingGear.getHoles().set(holeOfChangingGear.getNumberOfHole(), holeOfChangingGear);
                            downNeighbourOfChangingGear.getHoles().set(holeOfDownNeighbourOfChangingGear.getNumberOfHole(), holeOfDownNeighbourOfChangingGear);
                        }
                    }
                }

                if (upperNeighbourOfChangingGear != null) {
                    for (Gear.Hole holeOfUpperNeighbourOfChangingGear : upperNeighbourOfChangingGear.getHoles()) {
                        if (holeOfChangingGear.getDegree() % 180 == 0) {
                            continue;
                        }
                        if (holeOfChangingGear.getDegree() % 180 == (180 - holeOfUpperNeighbourOfChangingGear.getDegree()) % 180) {
                            holeOfChangingGear.setFree(false);
                            holeOfUpperNeighbourOfChangingGear.setFree(true);
                            int numberOfBallInHole = holeOfUpperNeighbourOfChangingGear.getNumberOfBall();
                            Ball fallenBall = this.getBalls().get(numberOfBallInHole);
                            fallenBall.setChosenGear(activeGear);
                            fallenBall.setChosenHoleInGear(holeOfChangingGear.getNumberOfHole());
                            this.getBalls().set(numberOfBallInHole, fallenBall);
                            changingGear.getHoles().set(holeOfChangingGear.getNumberOfHole(), holeOfChangingGear);
                            upperNeighbourOfChangingGear.getHoles().set(holeOfUpperNeighbourOfChangingGear.getNumberOfHole(), holeOfUpperNeighbourOfChangingGear);
                        }
                    }
                }
            }
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


    public int getStep() {
        return step;
    }

    private class Gutter {
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

    public static void main(String[] args) {
        Board board = new Board();

        var neighbourUp = new ArrayList<Integer>();
        neighbourUp.add(-1);
        neighbourUp.add(1);
        Gear gearUpp = new Gear(1, false, true, neighbourUp);
        gearUpp.getHoles().get(0).setNumberOfBall(0);
        gearUpp.getHoles().get(0).setFree(false);
        var neighbourMidd = new ArrayList<Integer>();
        neighbourMidd.add(0);
        neighbourMidd.add(2);
        Gear gearMiddle = new Gear(1, false, false, neighbourMidd);
        var neighbourDown = new ArrayList<Integer>();
        neighbourDown.add(1);
        neighbourDown.add(-1);
        Gear gearDown = new Gear(1, true, false, neighbourDown);
        var listGear = new ArrayList<Gear>();
        listGear.add(gearUpp);
        listGear.add(gearMiddle);
        listGear.add(gearDown);
        board.setGears(listGear);
        board.rebuild(60, 0);
        System.out.println(board.getBalls().size());
        if (board.getRightGutter().getHowManyBalls() == 5) {
            System.out.println("uspehhhhhhhhhhhhhhhhhhhhhhhhhhhh");
        }
        if (!board.getGears().get(0).getHoles().get(0).isFree()) {
            System.out.println("uspehhhhhhhhhhhhhhhhhhhhhhhhhhhh");
        }
        if (board.getBalls().get(0).getChosenGear() == 0) {
            System.out.println("uspehhhhhhhhhhhhhhhhhhhhhhhhhhhh");
        }

        board.rebuild(260, 1);
        System.out.println(board.getBalls().size());
        board.rebuild(40, 0);
        System.out.println(board.getBalls().size());
        board.rebuild(260, 2);
        System.out.println(board.getBalls().size());
        board.rebuild(200, 1);
        System.out.println(board.getBalls().size());
        board.rebuild(220, 2);
        System.out.println(board.getBalls().size());

    }
}
