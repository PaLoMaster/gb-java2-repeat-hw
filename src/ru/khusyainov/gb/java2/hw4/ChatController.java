package ru.khusyainov.gb.java2.hw4;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatController {
    @FXML
    TextArea history;
    @FXML
    TextField message;
    @FXML
    Button sendButton;

    @FXML
    public void sendMessage(ActionEvent actionEvent) {
        history.appendText(message.getText() + "\n");
        message.clear();
        message.requestFocus();
    }
}