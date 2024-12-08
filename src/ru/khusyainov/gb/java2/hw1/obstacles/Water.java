package ru.khusyainov.gb.java2.hw1.obstacles;

import ru.khusyainov.gb.java2.hw1.competitors.Competitor;

public class Water extends Obstacle {
    private final int distance;

    public Water(int distance) {
        this.distance = distance;
    }

    @Override
    public void doIt(Competitor competitor) {
        competitor.swim(distance);
    }

    @Override
    public String toString() {
        return "Water{" +
                "distance=" + distance +
                '}';
    }
}