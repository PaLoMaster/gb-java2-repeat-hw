package ru.khusyainov.gb.java2.hw3;

import java.util.*;

public class TelephoneBook {
    private final Map<String, Set<String>> book = new HashMap<>();

    public void add(String surname, String telephone) {
        if (!book.containsKey(surname)) {
            book.put(surname, new HashSet<>());
        }
        book.get(surname).add(telephone);
    }

    public List<String> get(String surname) {
        return book.get(surname).stream().toList();
    }
}
