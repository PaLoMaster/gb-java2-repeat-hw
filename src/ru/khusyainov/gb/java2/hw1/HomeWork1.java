package ru.khusyainov.gb.java2.hw1;

import ru.khusyainov.gb.java2.hw1.competitors.*;
import ru.khusyainov.gb.java2.hw1.obstacles.*;

public class HomeWork1 {
    public static void main(String[] args) {
        Competitor[] competitors = {new Human("Боб"), new Cat("Барсик"), new Dog("Бобик")};
        Obstacle[] course = {new Cross(400), new Wall(8), new Water(1)};
        for (Competitor c : competitors) {
            for (Obstacle o : course) {
                o.doIt(c);
                if (!c.isOnDistance()) break;
            }
        }
        System.out.println("==============");
        for (Competitor c : competitors) {
            c.showResult();
        }
    }
}