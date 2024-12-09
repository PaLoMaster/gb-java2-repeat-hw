package ru.khusyainov.gb.java2.hw1.competitors;

public abstract class Animal implements Competitor {
    String name;
    int maxRunDistance;
    float maxJumpHeight;
    int maxSwimDistance;
    boolean onDistance;

    public Animal(String name, int maxRunDistance, float maxJumpHeight, int maxSwimDistance) {
        this.name = name;
        this.maxRunDistance = maxRunDistance;
        this.maxJumpHeight = maxJumpHeight;
        this.maxSwimDistance = maxSwimDistance;
        onDistance = true;
    }

    private <T extends Comparable<T>> void executor(T executionLimit, T executionRate, String executionDescriptor) {
        String pattern = "%s named as \"%s\", can %s %s, must %s,%s completed\n";
        onDistance = executionLimit.compareTo(executionRate) >= 0;
        System.out.printf(pattern, getClass().getSimpleName(), name, executionDescriptor, executionLimit,
                executionRate, (onDistance ? "" : " not"));
    }

    @Override
    public void run(int runDistance) {
        executor(maxRunDistance, runDistance, "run");
    }

    @Override
    public void jump(float jumpHeight) {
        executor(maxJumpHeight, jumpHeight, "jump");
    }

    @Override
    public void swim(int swimDistance) {
        executor(maxSwimDistance, swimDistance, "swim");
    }

    @Override
    public boolean isOnDistance() {
        return onDistance;
    }

    @Override
    public void showResult() {
        System.out.println(getClass().getSimpleName() + " " + name + ": " + onDistance);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "name='" + name + '\'' +
                ", maxRunDistance=" + maxRunDistance +
                ", maxJumpHeight=" + maxJumpHeight +
                ", maxSwimDistance=" + maxSwimDistance +
                ", onDistance=" + onDistance +
                '}';
    }
}