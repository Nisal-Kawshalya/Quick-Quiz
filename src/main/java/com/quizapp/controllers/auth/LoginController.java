package com.quizapp.controllers.auth;

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
            return;
        }

        if (email.equals("demo@example.com") && password.equals("password123")) {
            errorLabel.setText("Login successful!");
            errorLabel.setStyle("-fx-text-fill: green;");
        } else {
            errorLabel.setText("Invalid email or password.");
        }
    }

    @FXML
    private void goToRegister() {
        Router.goTo("register");
    }
}
