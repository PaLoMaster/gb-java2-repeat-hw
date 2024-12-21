package ru.khusyainov.gb.java2.hw7.client.console;

import ru.khusyainov.gb.java2.hw7.ChatHelper;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class HomeWork7ConsoleClient extends Thread {
    public static void main(String[] args) throws IOException {
        new HomeWork7ConsoleClient().start();
    }

    private boolean notAuthorized;

    @Override
    public void run() {
        try {
            Socket socket = new Socket(ChatHelper.SERVER_HOST, ChatHelper.SERVER_PORT);
            Scanner fromServerIn = new Scanner(socket.getInputStream());
            PrintStream toServerOut = new PrintStream(socket.getOutputStream());
            Scanner fromMe = new Scanner(System.in);
            setNotAuthorized(true);
            Thread fromServerThread = new Thread(() -> {
                try {
                    while (!Thread.interrupted() && fromServerIn.hasNextLine()) {
                        String fromServerMessage = fromServerIn.nextLine();
                        if (ChatHelper.isAuthorizedStatus(fromServerMessage)) {
                            setNotAuthorized(false);
                            System.out.println("Вы авторизованы. Если Вы видите запрос авторизации, то нажмите Enter.");
                            break;
                        }
                        System.out.println(fromServerMessage);
                    }
                    while (!Thread.interrupted() && fromServerIn.hasNextLine()) {
                        System.out.println(fromServerIn.nextLine());
                    }
                    System.out.println(ChatHelper.getServerDisconnectedMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            fromServerThread.start();
            while (isNotAuthorized()) {
                System.err.printf("Для входа введите без кавычек:\n%s\n",
                        ChatHelper.getLoginCommand("\"login\"", "\"password\""));
                String stringToServer = fromMe.nextLine();
                if (isNotAuthorized()) {
                    toServerOut.println(stringToServer);
                    if (toServerOut.checkError()) {
                        System.out.println(ChatHelper.getAuthorizationNotSentMessage());
                    }
                }
            }
            System.out.println(ChatHelper.addTime(
                    "Для отправки личного сообщения введите без кавычек:\n/w \"nick\" \"сообщение\"\n" +
                    "Для выхода введите:\n" + ChatHelper.LOGOUT_COMMAND));
            while (true) {
                String toServerMessage = fromMe.nextLine();
                toServerOut.println(toServerMessage);
                if (toServerOut.checkError()) {
                    System.out.println(ChatHelper.getMessageNotSentMessage());
                }
                if (ChatHelper.isLogoutCommand(toServerMessage)) {
                    break;
                }
            }
            fromServerThread.interrupt();
            fromMe.close();
            toServerOut.close();
            fromServerIn.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized boolean isNotAuthorized() {
        return notAuthorized;
    }

    public synchronized void setNotAuthorized(boolean notAuthorized) {
        this.notAuthorized = notAuthorized;
    }
}
