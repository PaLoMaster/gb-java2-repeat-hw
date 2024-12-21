package ru.khusyainov.gb.java2.hw7;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ChatHelper {
    public static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 8189;
    public static final String SPACE_REGEX = "\\s";
    private static final String LOGIN_REGEX = "\\b[a-zA-Zа-яА-я0-9!#$%&'()*+,-.:?@^_`]{6,32}\\b";
    private static final String NICK_REGEX = "\\b[a-zA-Zа-яА-я0-9!#$%&'()*+,-.:?@^_`]{5,32}\\b";
    private static final String PASSWORD_REGEX = "\\b[a-zA-Zа-яА-я0-9!#$%&'()*+,-.:?@^_`]{5,64}\\b";
    private static final String MESSAGE_REGEX = ".+";
    private static final String LOGIN_COMMAND = "/auth";
    private static final String AUTHORIZATION_NOT_SENT = "Логин/пароль не отправлены!";
    private static final String LOGIN_COMMAND_REGEX =
            "^" + LOGIN_COMMAND + SPACE_REGEX + LOGIN_REGEX + SPACE_REGEX + PASSWORD_REGEX;
    private static final String AUTHORIZED_STATUS = LOGIN_COMMAND + "ok";
    private static final String PRIVATE_MESSAGE_REGEX =
            "^/w" + SPACE_REGEX + NICK_REGEX + SPACE_REGEX + MESSAGE_REGEX;
    private static final String MESSAGE_NOT_SENT = "Сообщение не отправлено!";
    public static final String LOGOUT_COMMAND = "/end";
    private static final String SERVER_DISCONNECTED = "Связь с сервером окончена/потеряна.";
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private static final String COLON = ": ";
    private static final String SPACE = " ";
    private static final String MY_NAME = "Я";

    public static String getAuthorizationNotSentMessage() {
        return getDateAndTime() + SPACE + AUTHORIZATION_NOT_SENT;
    }

    public static String getLoginCommand(String login, String password) {
        return LOGIN_COMMAND + SPACE + login + SPACE + password;
    }

    public static boolean isAuthorizeCommand(String message) {
        return message != null && message.startsWith(LOGIN_COMMAND);
    }

    private static String[] getPartsIfCommand(String commandRegex, String message) {
        String[] parts = new String[2];
        if (message.matches(commandRegex)) {
            parts[0] = message.split(SPACE_REGEX)[1];
            parts[1] = message.substring(message.indexOf(parts[0]) + parts[0].length() + 1);
        }
        return parts[0] == null ? null : parts;
    }

    public static String[] getPartsIfLoginCommand(String message) {
        return getPartsIfCommand(LOGIN_COMMAND_REGEX, message);
    }

    public static String getAuthorizedStatus(String nick) {
        return AUTHORIZED_STATUS + SPACE + nick;
    }

    public static boolean isAuthorizedStatus(String message) {
        return message != null && message.startsWith(AUTHORIZED_STATUS);
    }

    public static String getMessageNotSentMessage() {
        return getDateAndTime() + SPACE + MESSAGE_NOT_SENT;
    }

    public static String[] getPartsIfPrivateMessageCommand(String message) {
        return getPartsIfCommand(PRIVATE_MESSAGE_REGEX, message);
    }

    public static boolean isLogoutCommand(String message) {
        return message != null && message.startsWith(LOGOUT_COMMAND);
    }

    public static String getServerDisconnectedMessage() {
        return getDateAndTime() + SPACE + SERVER_DISCONNECTED;
    }

    private static String getDateAndTime() {
        return ZonedDateTime.now().format(DATE_TIME_FORMAT);
    }

    public static String addTime(String message) {
        return getDateAndTime() + COLON + message;
    }

    public static String addTimeToSomeonesMessage(String name, String message) {
        return getDateAndTime() + SPACE + name + COLON + message;
    }

    public static String addTimeToMyMessage(String message) {
        return addTimeToSomeonesMessage(MY_NAME, message);
    }

    public static String addTimeToMyPrivateMessage(String toNick, String message) {
        return addTimeToSomeonesMessage(MY_NAME + SPACE + toNick, message);
    }
}
