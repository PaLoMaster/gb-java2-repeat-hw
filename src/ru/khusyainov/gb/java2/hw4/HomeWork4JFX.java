package ru.khusyainov.gb.java2.hw4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.util.Objects;

public class HomeWork4JFX extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("chat.fxml")));
        root.getStylesheets().add(Objects.requireNonNull(
                getClass().getClassLoader().getResource("styles.css")).toExternalForm());
        stage.setTitle("Чат");
        Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
        float windowRatio = 0.6f;
        int width = (int) (screenSize.getWidth() * windowRatio);
        int height = (int) (screenSize.getHeight() * windowRatio);
        stage.setScene(new Scene(root, width, height));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}