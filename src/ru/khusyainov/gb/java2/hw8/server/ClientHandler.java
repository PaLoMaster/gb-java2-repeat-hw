package ru.khusyainov.gb.java2.hw8.server;

import ru.khusyainov.gb.java2.hw8.ChatHelper;
import ru.khusyainov.gb.java2.hw7.Client;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class ClientHandler {
    private MyServer myServer;
    private Socket serverSocket;
    private Scanner fromClientIn;
    private PrintStream toClientOut;
    private Client client;

    public ClientHandler(MyServer myServer, Socket serverSocket) {
        try {
            this.myServer = myServer;
            this.serverSocket = serverSocket;
            serverSocket.setSoTimeout(120000);
            fromClientIn = new Scanner(serverSocket.getInputStream());
            toClientOut = new PrintStream(serverSocket.getOutputStream());
            new Thread(() -> {
                try {
                    authenticate();
                    readAndSendMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                closeConnection();
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
            closeConnection();
        }
    }

    public String getNick() {
        return client == null ? null : client.getNick();
    }

    public Client getClient() {
        return client;
    }

    private void authenticate() throws IOException {
        while (true) {
            if (fromClientIn.hasNextLine()) {
                String fromClientMessage = fromClientIn.nextLine();
                if (ChatHelper.isAuthorizeCommand(fromClientMessage)) {
                    String[] parts = ChatHelper.getPartsIfLoginCommand(fromClientMessage);
                    if (parts != null) {
                        client = myServer.getAuthService().getClientByLoginPassword(parts[0], parts[1]);
                    }
                    if (client != null) {
                        if (!myServer.isClientOnline(client.getNick())) {
                            sendMessageToClient(ChatHelper.getAuthorizedStatus(getNick()));
                            myServer.subscribe(this);
                            serverSocket.setSoTimeout(0);
                            return;
                        } else {
                            sendMessageToClient(ChatHelper.addTime("Клиент с таким логином уже в сети."));
                        }
                    } else {
                        sendMessageToClient(ChatHelper.addTime("Неверные логин/пароль."));
                    }
                } else {
                    sendMessageToClient(ChatHelper.addTime("Вы не авторизованы. Присланное Вами сообщение" +
                            " отличается от запроса авторизации, поэтому не обработано."));
                }
            } else {
                throw new IOException("Клиент отключился, не авторизовался.");
            }
        }
    }

    private void readAndSendMessages() {
        while (true) {
            String fromClientMessage;
            String toClientNick = null;
            if (fromClientIn.hasNextLine()) {
                fromClientMessage = fromClientIn.nextLine();
                String[] parts = ChatHelper.getPartsIfPrivateMessageCommand(fromClientMessage);
                if (parts != null) {
                    toClientNick = parts[0];
                    fromClientMessage = parts[1];
                }
            } else {
                fromClientMessage = ChatHelper.LOGOUT_COMMAND;
            }
            if (ChatHelper.isLogoutCommand(fromClientMessage)) {
                closeConnection();
                break;
            }
            if (toClientNick != null) {
                myServer.sendMessageToClient(this, fromClientMessage, toClientNick);
            } else {
                myServer.sendMessageToClients(this, fromClientMessage);
            }
        }
    }

    public void sendMessageToClient(String message) {
        toClientOut.println(message);
    }

    public void closeConnection() {
        myServer.unsubscribe(this);
        fromClientIn.close();
        toClientOut.close();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean equals(String nick) {
        return client != null && client.equals(nick);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientHandler that = (ClientHandler) o;
        return Objects.equals(client, that.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client);
    }
}
