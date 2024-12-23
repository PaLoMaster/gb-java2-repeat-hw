package ru.khusyainov.gb.java2.hw8.client.console;

import ru.khusyainov.gb.java2.hw8.ChatHelper;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class HomeWork8ConsoleClient extends Thread {
    public static void main(String[] args) throws IOException {
        new HomeWork8ConsoleClient().start();
    }

    private Socket socket;
    private Scanner fromServerIn;
    private PrintStream toServerOut;
    private Scanner fromMe;
    private Thread fromServerThread;
    private final String COMMAND_LOGOUT = "\nДля выхода введите:\n" + ChatHelper.LOGOUT_COMMAND;
    private final String COMMANDS_LOGIN = "Для входа введите без кавычек:\n" +
            ChatHelper.getLoginCommand("\"login\"", "\"password\"") + COMMAND_LOGOUT;
    private final String COMMANDS_MESSAGING = "Для отправки личного сообщения введите без кавычек:\n" +
            "/w \"nick\" \"сообщение\"\nДля выхода введите:\n" + ChatHelper.LOGOUT_COMMAND;
    private boolean authorized;

    @Override
    public void run() {
        try {
            fromMe = new Scanner(System.in);
            while (true) {
                setAuthorized(false);
                String toServerMessage = login();
                if (ChatHelper.isLogoutCommand(toServerMessage)) {
                    break;
                }
                readAndSendMessages();
            }
            fromMe.close();
            serverDisconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized boolean isAuthorized() {
        return authorized;
    }

    public synchronized void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    private void serverConnect() throws IOException {
        if (socket == null || !socket.isConnected()) {
            socket = new Socket(ChatHelper.SERVER_HOST, ChatHelper.SERVER_PORT);
            fromServerIn = new Scanner(socket.getInputStream());
            toServerOut = new PrintStream(socket.getOutputStream());
            if (fromServerThread != null && !fromServerThread.isInterrupted()) {
                fromServerThread.interrupt();
            }
            fromServerThread = new Thread(() -> {
                try {
                    while (!Thread.interrupted() && fromServerIn.hasNextLine()) {
                        String fromServerMessage = fromServerIn.nextLine();
                        String nick = ChatHelper.getNickIfAuthorizedStatus(fromServerMessage);
                        if (nick != null) {
                            setAuthorized(true);
                            System.err.println("Вы авторизованы с ником " + nick +
                                    ". Если не видите сообщения с командами для общения, то нажмите Enter.");
                            break;
                        }
                        System.out.println(fromServerMessage);
                    }
                    while (!Thread.interrupted() && fromServerIn.hasNextLine()) {
                        System.out.println(fromServerIn.nextLine());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(ChatHelper.getServerDisconnectedMessage());
                if (isAuthorized()) {
                    setAuthorized(false);
                }
                serverDisconnect();
            });
            fromServerThread.start();
        }
    }

    private String login() throws IOException {
        while (!isAuthorized()) {
            System.err.println(COMMANDS_LOGIN);
            String toServerMessage = fromMe.nextLine();
            if (!isAuthorized()) {
                if (ChatHelper.isAuthorizeCommand(toServerMessage)) {
                    serverConnect();
                    toServerOut.println(toServerMessage);
                    if (toServerOut.checkError()) {
                        System.out.println(ChatHelper.getAuthorizationNotSentMessage());
                    }
                } else if (ChatHelper.isLogoutCommand(toServerMessage)) {
                    return toServerMessage;
                } else {
                    System.err.println(ChatHelper.getAuthorizationNotFullMessage());
                }
            }
        }
        return null;
    }

    private void readAndSendMessages() {
        String toServerMessage;
        System.out.println(COMMANDS_MESSAGING);
        do {
            toServerMessage = fromMe.nextLine();
            toServerOut.println(toServerMessage);
            if (toServerOut.checkError()) {
                System.out.println(ChatHelper.getMessageNotSentMessage());
            }
        } while (!ChatHelper.isLogoutCommand(toServerMessage));
    }

    private void serverDisconnect() {
        if (fromServerThread != null) {
            fromServerThread.interrupt();
            fromServerThread = null;
        }
        if (toServerOut != null) {
            toServerOut.close();
            toServerOut = null;
        }
        if (fromServerIn != null) {
            fromServerIn.close();
            fromServerIn = null;
        }
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
