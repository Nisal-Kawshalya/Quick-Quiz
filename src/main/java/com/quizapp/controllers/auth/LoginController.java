package com.quizapp.controllers.auth;

import com.quizapp.models.Session;
import com.quizapp.models.User;
import com.quizapp.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {

        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        /*
         * DEMO ROLE-BASED LOGIN
         * (Later we will replace this with Database logic)
         */

        if (email.equals("teacher@example.com")) {

            // Teacher login
            Session.setUser(new User(email, "TEACHER"));
            Router.goTo("teacher");

        } else if (email.equals("student@example.com")) {

            // Student login
            Session.setUser(new User(email, "STUDENT"));
            Router.goTo("student");

        } else {
            errorLabel.setText("Invalid email or password.");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void goToRegister() {
        Router.goTo("register");
    }
}
