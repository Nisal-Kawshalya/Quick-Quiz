package com.quizapp.controllers.auth;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.quizapp.routing.Router;


public class RegisterController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleBox;

    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        roleBox.getItems().addAll("STUDENT", "TEACHER");
    }

    @FXML
    private void handleRegister() {

        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String role = roleBox.getValue();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            messageLabel.setText("Please fill in all fields.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // TEMPORARY register logic
        messageLabel.setText("Registration successful as " + role + "!");
        messageLabel.setStyle("-fx-text-fill: green;");
    }

    @FXML
    private void goToLogin() {
        // Navigation logic will be added in Router step
       Router.goTo("login");
    }

    
}
