package ru.khusyainov.gb.java2.hw1.obstacles;

import ru.khusyainov.gb.java2.hw1.competitors.Competitor;
import ru.khusyainov.gb.java2.hw1.competitors.Team;

import java.util.Arrays;

public class Course {
    Obstacle[] obstacles;

    public Course(Obstacle[] obstacles) {
        this.obstacles = obstacles;
    }

    public void doIt(Team team) {
        for (Competitor competitor : team.getCompetitors()) {
            for (Obstacle obstacle : obstacles) {
                obstacle.doIt(competitor);
                if (!competitor.isOnDistance()) {
                    break;
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Course{" +
                "obstacles=" + Arrays.toString(obstacles) +
                '}';
    }
}