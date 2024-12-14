package ru.khusyainov.gb.java2.hw3;

import java.util.*;

public class HomeWork3 {
    private static final Random random = new Random();
    private static final String[] vocabulary = {"Иванов", "Петров", "Сидоров", "Абдулахметов", "Наберидзе", "Рустамов", "Махметов"};
    private static final int minimumSurnames = 10;
    private static final int maximumSurnames = 20;
    private static final int[] telephoneNumbersCount = {10, 7};

    public static void main(String[] args) {
        String[] strings = new String[random.nextInt(minimumSurnames, maximumSurnames + 1)];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = vocabulary[random.nextInt(vocabulary.length)];
        }
        System.out.println("Original array (" + strings.length + " words):\n" + Arrays.toString(strings));
        Map<String, Integer> counter = new HashMap<>();
        for (String string : strings) {
            counter.put(string, counter.getOrDefault(string, 0) + 1);
        }
        System.out.println(counter.size() + " original words found:\n" + counter.keySet());
        System.out.println("Counted words (word=count):\n" + counter);
        TelephoneBook book = new TelephoneBook();
        for (String string : strings) {
            book.add(string, getRandomTelephone("", null));
        }
        System.out.println("Telephone book (surname: telephones):");
        for (String k : counter.keySet()) {
            System.out.println(k + ": " + book.get(k));
        }
    }

    private static String getRandomTelephone(String start, Integer numbersCount) {
        if (numbersCount == null) {
            numbersCount = random.nextInt(telephoneNumbersCount.length);
            start += numbersCount == 0 ? "+7" : "";
            return getRandomTelephone(start, telephoneNumbersCount[numbersCount]);
        } else if (numbersCount == 0) {
            return start;
        } else {
            return getRandomTelephone(start + random.nextInt(10), --numbersCount);
        }
    }
}