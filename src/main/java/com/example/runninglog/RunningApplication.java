package com.example.runninglog;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class RunningApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RunningApplication.class.getResource("running-view.fxml"));
        double fixedWidth = 800;
        double fixedHeight = 550;
        Scene scene = new Scene(fxmlLoader.load(), fixedWidth, fixedHeight);
        stage.setTitle("Running Log");
        stage.setMinWidth(fixedWidth);
        stage.setMaxWidth(fixedWidth);
        stage.setMinHeight(fixedHeight);
        stage.setMaxHeight(fixedHeight);

        var appIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sneakers.png")));
        stage.getIcons().add(appIcon);
        stage.setScene(scene);
        stage.show();
    }
}
