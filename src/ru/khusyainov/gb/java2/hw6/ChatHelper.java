package ru.khusyainov.gb.java2.hw6;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ChatHelper {
    public static final String CLIENT_END_STRING = "/end";
    public static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 8189;
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private static final String COLON = ": ";
    private static final String SPACE = " ";
    private static final String MY_NAME = "Я";
    private static final String SERVER_NAME = "Сервер";
    private static final String CLIENT_NAME = "Клиент";

    private static String getDateAndTime() {
        return ZonedDateTime.now().format(DATE_TIME_FORMAT);
    }

    public static String addTime(String message) {
        return getDateAndTime() + COLON + message;
    }

    private static String addTimeToSomeonesMessage(String name, String message) {
        return getDateAndTime() + SPACE + name + COLON + message;
    }

    public static String addTimeToMyMessage(String message) {
        return addTimeToSomeonesMessage(MY_NAME, message);
    }

    public static String addTimeToServerMessage(String message) {
        return addTimeToSomeonesMessage(SERVER_NAME, message);
    }

    public static String addTimeToClientMessage(String message) {
        return addTimeToSomeonesMessage(CLIENT_NAME, message);
    }
}
