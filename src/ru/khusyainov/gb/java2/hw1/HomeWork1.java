package ru.khusyainov.gb.java2.hw1;

import ru.khusyainov.gb.java2.hw1.competitors.*;
import ru.khusyainov.gb.java2.hw1.obstacles.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeWork1 {
    static Number[] animalMinimumLimits = {200, 0.5f, 3};
    static Number[] dogsMinimumLimits = {300, 0.3f, 8};
    static Number[] catsMinimumLimits = {100, 1.8f, 1};
    static Number[] humanMinimumLimits = {1000, 2.5f, 15};
    static Number[] animalLimitsDispersion = {300, 0.5f, 3};
    static Random random = new Random();
    static int teamsLimit = 10;
    static int teamCompetitorsMinimum = 3;
    static int teamCompetitorsLimit = 6;
    static int humansCounter, dogsCounter, catsCounter;

    public static void main(String[] args) {
        int runDistance = getIntegerRandom(animalMinimumLimits, CompetitionType.RUN);
        float jumpHeight = getFloatRandom(animalMinimumLimits);
        int swimDistance = getIntegerRandom(animalMinimumLimits, CompetitionType.SWIM);
        Course course = new Course(new Obstacle[]{new Cross(runDistance), new Wall(jumpHeight),
                new Water(swimDistance)});
        System.out.println("Course created: " + course);
        int teamsCount = random.nextInt(1, teamsLimit);
        Team[] teams = new Team[teamsCount];
        int teamCompetitorsCount = random.nextInt(teamCompetitorsMinimum, teamCompetitorsLimit);
        System.out.println(teamsCount + " teams creating of " + teamCompetitorsCount + " competitors each:");
        for (int i = 0; i < teamsCount; i++) {
            Competitor[] team = new Competitor[teamCompetitorsCount];
            for (int j = 0; j < teamCompetitorsCount; j++) {
                team[j] = getRandomCompetitor();
            }
            teams[i] = new Team("Team " + (i + 1), team);
            teams[i].showTeam();
        }
        System.out.println("\n\nCompetition start:\n");
        for (Team team : teams) {
            course.doIt(team);
            System.out.println();
            team.showResults();
            System.out.println();
        }
    }

    private static Competitor getRandomCompetitor() {
        AnimalType competitorType = new ArrayList<>(List.of(AnimalType.values()))
                .get(random.nextInt(AnimalType.values().length));
        Number[] minimumLimits = null;
        switch (competitorType) {
            case HUMAN -> minimumLimits = humanMinimumLimits;
            case DOG -> minimumLimits = dogsMinimumLimits;
            case CAT -> minimumLimits = catsMinimumLimits;
        }
        int runDistance = getIntegerRandom(minimumLimits, CompetitionType.RUN);
        float jumpHeight = getFloatRandom(minimumLimits);
        int swimDistance = getIntegerRandom(minimumLimits, CompetitionType.SWIM);
        Competitor competitor = null;
        switch (competitorType) {
            case HUMAN -> competitor = new Human("Human " + ++humansCounter, runDistance, jumpHeight, swimDistance);
            case DOG -> competitor = new Dog("Dog " + ++dogsCounter, runDistance, jumpHeight, swimDistance);
            case CAT -> competitor = new Cat("Cat " + ++catsCounter, runDistance, jumpHeight, swimDistance);
        }
        return competitor;
    }

    static int getIntegerRandom(Number[] animalMinimumLimits, CompetitionType competitionType) {
        int limitIndex = competitionType.ordinal();
        return animalMinimumLimits[limitIndex].intValue() +
                random.nextInt(animalLimitsDispersion[limitIndex].intValue());
    }

    static float getFloatRandom(Number[] animalMinimumLimits) {
        int limitIndex = CompetitionType.JUMP.ordinal();
        return animalMinimumLimits[limitIndex].floatValue() +
                random.nextFloat(animalLimitsDispersion[limitIndex].floatValue());
    }
}