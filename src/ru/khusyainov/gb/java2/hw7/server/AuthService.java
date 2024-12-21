package ru.khusyainov.gb.java2.hw7.server;

import ru.khusyainov.gb.java2.hw7.Client;

public interface AuthService {
    void start();
    Client getClientByLoginPassword(String login, String password);
    void stop();
}
