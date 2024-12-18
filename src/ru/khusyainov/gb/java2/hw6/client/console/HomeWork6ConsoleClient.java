package ru.khusyainov.gb.java2.hw6.client.console;

import ru.khusyainov.gb.java2.hw6.ChatHelper;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class HomeWork6ConsoleClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(ChatHelper.SERVER_HOST, ChatHelper.SERVER_PORT);
        Scanner fromServerIn = new Scanner(socket.getInputStream());
        Scanner fromMe = new Scanner(System.in);
        PrintStream toServerOut = new PrintStream(socket.getOutputStream());
        Thread fromServerThread = new Thread(() -> {
            try {
                while (!Thread.interrupted() && fromServerIn.hasNextLine()) {
                    System.out.println(ChatHelper.addTimeToServerMessage(fromServerIn.nextLine()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        fromServerThread.start();
        while (true) {
            String stringToServer = fromMe.nextLine();
            toServerOut.println(stringToServer);
            if (stringToServer.equalsIgnoreCase(ChatHelper.CLIENT_END_STRING)) {
                fromServerThread.interrupt();
                break;
            }
        }
        fromMe.close();
        toServerOut.close();
        fromServerIn.close();
        socket.close();
    }
}
