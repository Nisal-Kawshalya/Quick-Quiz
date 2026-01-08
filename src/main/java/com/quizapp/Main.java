package com.quizapp;

import com.quizapp.routing.Router;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/MainLayout.fxml")
        );

        BorderPane root = loader.load();

        Router.setRoot(root);
        Router.goTo("login");   // FIRST PAGE

        Scene scene = new Scene(root, 900, 600);
        stage.setTitle("JavaFX Quiz Application");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
