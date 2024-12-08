package ru.khusyainov.gb.java2.hw1.competitors;

public interface Competitor {
    void run(int distance);
    void jump(float height);
    void swim(int distance);
    boolean isOnDistance();
    void showResult();
}