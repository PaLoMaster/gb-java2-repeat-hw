package ru.khusyainov.gb.java2.hw1.competitors;

public interface Competitor {
    void run(int distance);

    void swim(int distance);

    void jump(int height);

    boolean isOnDistance();

    void showResult();
}