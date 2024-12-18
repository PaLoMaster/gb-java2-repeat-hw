package ru.khusyainov.gb.java2.hw6.server;

import ru.khusyainov.gb.java2.hw6.ChatHelper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class HomeWork6ConsoleServer {
    private static final String SERVER_STARTED_MESSAGE = "Сервер запущен, ожидаем подключения...";
    private static final String CLIENT_CONNECTED_MESSAGE = "Клиент подключился";
    private static final String HI_TO_CLIENT = "Привет!";
    private static final String CLIENT_DISCONNECTED_MESSAGE =
            "Клиент отключился. Для завершения нажмите Enter. Введённое сообщение не будет отправлено.";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(ChatHelper.SERVER_PORT)) {
            System.out.println(SERVER_STARTED_MESSAGE);
            Socket socket = serverSocket.accept();
            System.out.println(CLIENT_CONNECTED_MESSAGE);
            Scanner fromClientIn = new Scanner(socket.getInputStream());
            Scanner fromMe = new Scanner(System.in);
            PrintStream toClientOut = new PrintStream(socket.getOutputStream());
            toClientOut.println(HI_TO_CLIENT);
            Thread toClientThread = new Thread(() -> {
                try {
                    while (!Thread.interrupted()) {
                        toClientOut.println(fromMe.nextLine());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            toClientThread.start();
            while (true) {
                String stringFromClient;
                if (fromClientIn.hasNextLine()) {
                    stringFromClient = fromClientIn.nextLine();
                } else {
                    stringFromClient = ChatHelper.CLIENT_END_STRING;
                }
                if (stringFromClient.equals(ChatHelper.CLIENT_END_STRING)) {
                    toClientThread.interrupt();
                    System.err.println(CLIENT_DISCONNECTED_MESSAGE);
                    break;
                }
                System.out.println(ChatHelper.addTimeToClientMessage(stringFromClient));
            }
            fromMe.close();
            fromClientIn.close();
            toClientOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
