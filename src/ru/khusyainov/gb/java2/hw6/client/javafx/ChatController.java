package ru.khusyainov.gb.java2.hw6.client.javafx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ru.khusyainov.gb.java2.hw6.ChatHelper;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ChatController implements Initializable {
    @FXML
    TextArea history;
    @FXML
    TextField message;
    @FXML
    Button sendButton;
    PrintStream toServerOut;

    @FXML
    public void sendMessage(ActionEvent actionEvent) {
        toServerOut.println(message.getText());
        if (!toServerOut.checkError()) {
            history.appendText(ChatHelper.addTimeToMyMessage(message.getText() + "\n"));
            message.clear();
            message.requestFocus();
        } else {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Сообщение не отправлено!");
                alert.showAndWait();
            });
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Socket socket = new Socket(ChatHelper.SERVER_HOST, ChatHelper.SERVER_PORT);
            Scanner fromServerIn = new Scanner(socket.getInputStream(), StandardCharsets.UTF_8);
            toServerOut = new PrintStream(socket.getOutputStream(), true, StandardCharsets.UTF_8);
            Thread fromServerThread = new Thread(() -> {
                try {
                    while (!Thread.interrupted() && fromServerIn.hasNextLine()) {
                        history.appendText(ChatHelper.addTimeToServerMessage(fromServerIn.nextLine() + "\n"));
                    }
                    history.appendText(ChatHelper.addTime("Соединение с сервером потеряно/закрыто."));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            fromServerThread.setDaemon(true);
            fromServerThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}