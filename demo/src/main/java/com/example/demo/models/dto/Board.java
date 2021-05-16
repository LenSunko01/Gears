package com.example.demo.models.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Board {
    private List<Gear> gears;
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


    public List<Gear> getGears() {
        return gears;
    }

    public void setGears(List<Gear> gears) {
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

            extractBallsFromLastGear(activeGear, changingGear);
            putBallsInFirstGear(activeGear, changingGear);

            for (var indexDownNeighbour : changingGear.getDownNeighbours()) {
                connectionHoles(changingGear, this.gears.get(indexDownNeighbour));
            }
            for (var indexUpperNeighbour : changingGear.getUpperNeighbours()) {
                connectionHoles(this.gears.get(indexUpperNeighbour), changingGear);
            }

        }

    }


    private void putBallsInFirstGear(int activeGear, Gear changingGear) {
        if (changingGear.isFirst()) {
            for (Gear.Hole holeOfChangingGear : changingGear.getHoles()) {
                if (holeOfChangingGear.isFree() && holeOfChangingGear.getDegree() == this.getLeftGutter().getDegree() ||
                        holeOfChangingGear.getDegree() == this.getRightGutter().getDegree()) {

                    if (holeOfChangingGear.getDegree() == this.getLeftGutter().getDegree()) {
                        this.getLeftGutter().setHowManyBalls(getLeftGutter().getHowManyBalls() - holeOfChangingGear.getCapacity());
                    }

                    if (holeOfChangingGear.getDegree() == this.getRightGutter().getDegree()) {
                        this.getRightGutter().setHowManyBalls(getRightGutter().getHowManyBalls() - holeOfChangingGear.getCapacity());
                    }
                    holeOfChangingGear.setFree(false);
                    changingGear.getHoles().set(holeOfChangingGear.getNumberOfHole(), holeOfChangingGear);
                }
            }
            var arrayOfGears = this.getGears();
            arrayOfGears.set(activeGear, changingGear);
        }
    }

    private void extractBallsFromLastGear(int activeGear, Gear changingGear) {
        if (changingGear.isLast()) {
            for (Gear.Hole holeOfChangingGear : changingGear.getHoles()) {
                if (!holeOfChangingGear.isFree() && holeOfChangingGear.getDegree() == this.getPot().getDegree() && !holeOfChangingGear.isFree()) {
                    this.getPot().setHowManyBalls(getPot().getHowManyBalls() + holeOfChangingGear.getCapacity());
                    holeOfChangingGear.setFree(true);

                    changingGear.getHoles().set(holeOfChangingGear.getNumberOfHole(), holeOfChangingGear);
                }
            }
            var arrayOfGears = this.getGears();
            arrayOfGears.set(activeGear, changingGear);
        }
    }

    private boolean connectionHoleWithGearsCenter(Gear upperGear, Gear downGear, Gear.Hole upperHole) {
        int upperRadius = upperGear.getRadius();
        int downRadius = downGear.getRadius();
        int sumRadius = upperRadius + downRadius;
        double deg = 90 - upperHole.getDegree();
        double x = sumRadius * Math.cos(Math.toRadians(deg));
        double y = sumRadius * Math.sin(Math.toRadians(deg));
        int mistake = 10;
        x += upperGear.getX();
        y += upperGear.getY();
        double dist = Math.sqrt(Math.pow((x - downGear.getX()), 2) + Math.pow((y - downGear.getY()), 2));
        return dist <= mistake;
    }

    private void connectionHoles(Gear upperGear, Gear downGear) {
        for (Gear.Hole holeUpperGear : upperGear.getHoles()) {
            if (!holeUpperGear.isFree() && connectionHoleWithGearsCenter(upperGear, downGear, holeUpperGear)) {
                for (Gear.Hole holeDownGear : downGear.getHoles()) {
                    if (holeDownGear.isFree()
                            && holeDownGear.getDegree() % 180 == (180 - holeUpperGear.getDegree()) % 180) {
                        holeDownGear.setFree(false);
                        holeUpperGear.setFree(true);
                        break;
                    }
                }
                break;
            }
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

    public static void main(String[] args) {
        Gear leftGear = new Gear(1, false, false, 2, 2, 2,
                Collections.singletonList(1), Collections.emptyList());
        Gear rightGear = new Gear(1, false, false, 3, 2, 1, Collections.emptyList(),
                Collections.singletonList(0));
        leftGear.getHoles().get(0).setFree(true);
        rightGear.getHoles().get(0).setFree(false);
        Board b = new Board();
        b.setGears(Arrays.asList(leftGear, rightGear));
        b.rebuild(90, 0);
        b.rebuild(-90,1);
        if (!b.getGears().get(1).getHoles().get(0).isFree()) {
            System.out.println("URA");
        } else {
            System.out.println("PIZDA");
        }

    }
}
