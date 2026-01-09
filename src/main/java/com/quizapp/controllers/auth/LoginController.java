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

        System.out.println("Login clicked: " + email); // 🔍 DEBUG

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        // 🔐 ROLE-BASED DEMO LOGIN
        if (email.equalsIgnoreCase("teacher@example.com")) {

            Session.setUser(new User(email, "TEACHER"));
            Router.goTo("teacher");

        } else if (email.equalsIgnoreCase("student@example.com")) {

            Session.setUser(new User(email, "STUDENT"));
            Router.goTo("student");

        } else {
            errorLabel.setText("Invalid email or password.");
        }
    }

    @FXML
    private void goToRegister() {
        Router.goTo("register");
    }
}
