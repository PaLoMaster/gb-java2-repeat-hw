package ru.khusyainov.gb.java2.hw1.obstacles;

import ru.khusyainov.gb.java2.hw1.competitors.Competitor;

public class Cross extends Obstacle {
    private final int distance;

    public Cross(int distance) {
        this.distance = distance;
    }

    @Override
    public void doIt(Competitor competitor) {
        competitor.run(distance);
    }

    @Override
    public String toString() {
        return "Cross{" +
                "distance=" + distance +
                '}';
    }
}