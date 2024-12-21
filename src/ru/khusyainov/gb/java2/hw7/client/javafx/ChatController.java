package ru.khusyainov.gb.java2.hw7.client.javafx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import ru.khusyainov.gb.java2.hw7.ChatHelper;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ChatController implements Initializable {
    @FXML
    private HBox loginPanel;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextArea history;
    @FXML
    private HBox messagePanel;
    @FXML
    private TextField message;
    private Socket socket;
    private Scanner fromServerIn;
    private PrintStream toServerOut;
    private Thread fromServerThread;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            socket = new Socket(ChatHelper.SERVER_HOST, ChatHelper.SERVER_PORT);
            fromServerIn = new Scanner(socket.getInputStream(), StandardCharsets.UTF_8);
            toServerOut = new PrintStream(socket.getOutputStream(), true, StandardCharsets.UTF_8);
            setAuthorized(false);
            fromServerThread = new Thread(() -> {
                try {
                    while (!Thread.interrupted() && fromServerIn.hasNextLine()) {
                        String fromServerMessage = fromServerIn.nextLine();
                        if (ChatHelper.isAuthorizedStatus(fromServerMessage)) {
                            setAuthorized(true);
                            break;
                        }
                        history.appendText(fromServerMessage + "\n");
                    }
                    while (!Thread.interrupted() && fromServerIn.hasNextLine()) {
                        history.appendText(fromServerIn.nextLine() + "\n");
                    }
                    history.appendText(ChatHelper.getServerDisconnectedMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            fromServerThread.setDaemon(true);
            fromServerThread.start();
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        fromServerThread.interrupt();
        toServerOut.println(ChatHelper.LOGOUT_COMMAND);
        toServerOut.close();
        fromServerIn.close();
        try {
            socket.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @FXML
    public void sendMessage(ActionEvent actionEvent) {
        String messageText = message.getText();
        toServerOut.println(messageText);
        if (!toServerOut.checkError()) {
            String[] parts = ChatHelper.getPartsIfPrivateMessageCommand(messageText);
            if (parts != null) {
                String toClientNick = parts[0];
                messageText = parts[1];
                history.appendText(ChatHelper.addTimeToMyPrivateMessage(toClientNick,messageText + "\n"));
            } else {
                history.appendText(ChatHelper.addTimeToMyMessage(messageText + "\n"));
            }
            message.clear();
            message.requestFocus();
        } else {
            showMessage(ChatHelper.getMessageNotSentMessage());
        }
    }

    @FXML
    public void login(ActionEvent actionEvent) {
        toServerOut.println(ChatHelper.getLoginCommand(loginField.getText(), passwordField.getText()));
        if (!toServerOut.checkError()) {
            history.appendText(ChatHelper.addTimeToMyMessage("Отправлен запрос авторизации " + loginField.getText() + "\n"));
            loginField.clear();
            passwordField.clear();
            loginField.requestFocus();
        } else {
            showMessage(ChatHelper.getAuthorizationNotSentMessage());
        }
    }

    private void showMessage(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(message);
            alert.showAndWait();
        });
    }

    private void setAuthorized(boolean authorized) {
        history.clear();
        loginPanel.setVisible(!authorized);
        loginPanel.setManaged(!authorized);
        messagePanel.setVisible(authorized);
        messagePanel.setManaged(authorized);
    }
}