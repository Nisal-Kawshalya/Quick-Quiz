package com.quizapp.routing;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class Router {

    private static BorderPane root;

    public static void setRoot(BorderPane rootPane) {
        root = rootPane;
    }

    public static void goTo(String page) {
        try {
            Parent view = null;

            switch (page) {
                case "login":
                    view = FXMLLoader.load(
                            Router.class.getResource("/fxml/auth/LoginView.fxml")
                    );
                    break;

                case "register":
                    view = FXMLLoader.load(
                            Router.class.getResource("/fxml/auth/RegisterView.fxml")
                    );
                    break;

                default:
                    throw new IllegalArgumentException("Unknown page: " + page);
            }

            root.setCenter(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
