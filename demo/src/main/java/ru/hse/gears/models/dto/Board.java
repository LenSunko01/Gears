package ru.hse.gears.models.dto;

import java.util.ArrayList;
import java.util.List;

import static ru.hse.gears.models.dto.constants.DTOConstants.*;

public class Board {
    private List<Gear> gears;
    private Gutter rightGutter = new Gutter(RIGHT_GUTTER_START_DEGREE);
    private Gutter leftGutter = new Gutter(LEFT_GUTTER_START_DEGREE);
    private Pot pot = new Pot();
    private final int step = STEP_DEGREE;

    public Board() {
    }

    public Board(Board other) {
        this.gears = other.gears;
        this.rightGutter = new Gutter(other.rightGutter);
        this.leftGutter = new Gutter(other.leftGutter);
        this.pot = new Pot(other.pot);
        List<Gear> newGears = new ArrayList<>();
        for (Gear gear : other.gears) {
            newGears.add(new Gear(gear));
        }
        this.gears = newGears;
    }

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

            for (Integer indexDownNeighbour : changingGear.getDownNeighbours()) {
                connectionHoles(changingGear, this.gears.get(indexDownNeighbour));
            }
            for (Integer indexUpperNeighbour : changingGear.getUpperNeighbours()) {
                connectionHoles(this.gears.get(indexUpperNeighbour), changingGear);
            }

        }

    }


    private void putBallsInFirstGear(int activeGear, Gear changingGear) {
        if (changingGear.isFirst()) {
            for (Gear.Hole holeOfChangingGear : changingGear.getHoles()) {
                if (holeOfChangingGear.isFree() && (isEqualDegrees(holeOfChangingGear.getDegree(), this.getLeftGutter().getDegree()) ||
                        isEqualDegrees(holeOfChangingGear.getDegree(), this.getRightGutter().getDegree()))) {

                    if (this.getLeftGutter().getHowManyBalls() > 0 && isEqualDegrees(holeOfChangingGear.getDegree(), this.getLeftGutter().getDegree())) {
                        this.getLeftGutter().setHowManyBalls(getLeftGutter().getHowManyBalls() - holeOfChangingGear.getCapacity());
                        holeOfChangingGear.setFree(false);
                    }

                    if (this.getRightGutter().getHowManyBalls() > 0 && isEqualDegrees(holeOfChangingGear.getDegree(), this.getRightGutter().getDegree())) {
                        this.getRightGutter().setHowManyBalls(getRightGutter().getHowManyBalls() - holeOfChangingGear.getCapacity());
                        holeOfChangingGear.setFree(false);
                    }
                }
            }
        }
    }

    private void extractBallsFromLastGear(int activeGear, Gear changingGear) {
        if (changingGear.isLast()) {
            for (Gear.Hole holeOfChangingGear : changingGear.getHoles()) {
                if (!holeOfChangingGear.isFree() && isEqualDegrees(holeOfChangingGear.getDegree(), this.getPot().getDegree()) &&
                        !holeOfChangingGear.isFree()) {
                    this.getPot().setHowManyBalls(getPot().getHowManyBalls() + holeOfChangingGear.getCapacity());
                    holeOfChangingGear.setFree(true);
                }
            }
            List<Gear> arrayOfGears = this.getGears();
            arrayOfGears.set(activeGear, changingGear);
        }
    }

    private boolean connectionHoleWithGearsCenter(Gear upperGear, Gear downGear, Gear.Hole upperHole) {
        double sumRadius = Math.sqrt(Math.pow((upperGear.getX() - downGear.getX()), 2) + Math.pow((upperGear.getY() - downGear.getY()), 2));
        double deg = 90 - upperHole.getDegree();
        double x = sumRadius * Math.cos(Math.toRadians(deg));
        double y = sumRadius * Math.sin(Math.toRadians(deg));
        double mistake = sumRadius * DIST_MISTAKE_COEF;
        x += upperGear.getX();
        y = upperGear.getY() - y;
        double dist = Math.sqrt(Math.pow((x - downGear.getX()), 2) + Math.pow((y - downGear.getY()), 2));
        return dist <= mistake;
    }

    private void connectionHoles(Gear upperGear, Gear downGear) {
        for (Gear.Hole holeUpperGear : upperGear.getHoles()) {
            if (!holeUpperGear.isFree() && connectionHoleWithGearsCenter(upperGear, downGear, holeUpperGear)) {
                for (Gear.Hole holeDownGear : downGear.getHoles()) {
                    if (holeDownGear.isFree()) {
                        if (checkDegreeEquals(holeUpperGear, holeDownGear, upperGear.getX(), downGear.getX())) {
                            holeDownGear.setFree(false);
                            holeUpperGear.setFree(true);
                        }
                    }
                }
            }
        }
    }

    private boolean isEqualDegrees(int first, int second) {
        return Math.abs(first - second) <= DEGREE_MISTAKE;
    }

    private boolean checkDegreeEquals(Gear.Hole upperGearHole, Gear.Hole downGearHole, double xUpperGear, double xDownGear) {
        if (upperGearHole.getDegree() == 180) {
            return downGearHole.getDegree() > NEAR_ZERO_FROM_FOURTH_QUATER_MISTAKE_DEGREE
                    || downGearHole.getDegree() < NEAR_ZERO_FROM_FIRST_QUATER_MISTAKE_DEGREE;
        }
        if (xUpperGear - xDownGear < 0) {
            return upperGearHole.getDegree() < 180 && downGearHole.getDegree() > 180 &&
                    isEqualDegrees(upperGearHole.getDegree(), downGearHole.getDegree() - 180);
        }

        return upperGearHole.getDegree() > 180 && downGearHole.getDegree() < 180 &&
                isEqualDegrees(downGearHole.getDegree(), upperGearHole.getDegree() - 180);
    }

    public boolean isAllBallsInPot() {
        return pot.howManyBalls == leftGutter.getHowManyBallsStart() + rightGutter.getHowManyBallsStart();
    }

    public int getStep() {
        return step;
    }

    public class Gutter {
        public Gutter() {
        }

        public Gutter(Gutter other) {
            this.degree = other.degree;
            this.howManyBalls = other.howManyBalls;
            this.howManyBallsStart = other.howManyBallsStart;
        }

        private int degree;
        private int howManyBalls = BALLS_START_IN_GUTTER;
        private int howManyBallsStart = BALLS_START_IN_GUTTER;

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

        public int getHowManyBallsStart() {
            return howManyBallsStart;
        }
    }

    public class Pot {
        public Pot() {
        }

        public Pot(Pot other) {
            this.degree = other.degree;
            this.howManyBalls = other.howManyBalls;
        }

        private int degree = POT_START_DEGREE;
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