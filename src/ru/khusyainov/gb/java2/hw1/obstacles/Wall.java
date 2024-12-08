package ru.khusyainov.gb.java2.hw1.obstacles;

import ru.khusyainov.gb.java2.hw1.competitors.Competitor;

public class Wall extends Obstacle {
    private int height;

    public Wall(int height) {
        this.height = height;
    }

    @Override
    public void doIt(Competitor competitor) {
        competitor.jump(height);
    }
}