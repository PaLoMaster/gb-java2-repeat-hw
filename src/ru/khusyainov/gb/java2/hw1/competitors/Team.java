package ru.khusyainov.gb.java2.hw1.competitors;

import java.util.Arrays;
import java.util.List;

public class Team {
    private final String name;
    private final Competitor[] competitors;

    public Team(String name, Competitor[] competitors) {
        this.name = name;
        this.competitors = competitors;
    }

    public Competitor[] getCompetitors() {
        return competitors;
    }

    public void showCompletedDistance() {
        List<Competitor> completed = Arrays.stream(competitors).filter(Competitor::isOnDistance).toList();
        if (completed.isEmpty()) {
            System.out.println("None of team " + name + " completed the distance.");
        } else {
            System.out.println("Competitors of team " + name + " who have completed the distance:");
            completed.forEach(Competitor::showResult);
        }
    }

    public void showTeam() {
        System.out.println(this);
    }

    public void showResults() {
        showCompletedDistance();
    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name + '\'' +
                ", competitors=" + Arrays.toString(competitors) +
                '}';
    }
}