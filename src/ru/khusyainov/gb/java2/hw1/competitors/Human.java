package ru.khusyainov.gb.java2.hw1.competitors;

public class Human extends Animal implements Competitor {
    public Human(String name, int maxRunDistance, float maxJumpHeight, int maxSwimDistance) {
        super(name, maxRunDistance, maxJumpHeight, maxSwimDistance);
    }
}