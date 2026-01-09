package com.quizapp.controllers.auth;

import com.quizapp.firebase.FirebaseAuthService;
import com.quizapp.firebase.RealtimeDatabaseService;
import com.quizapp.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleBox;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        roleBox.getItems().addAll("STUDENT", "TEACHER");
    }

    @FXML
    private void handleRegister() {

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String role = roleBox.getValue();

        if (name.isEmpty() || email.isEmpty() ||
                password.isEmpty() || role == null) {
            messageLabel.setText("Please fill in all fields.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            boolean registered =
                    FirebaseAuthService.register(email, password);

            if (!registered) {
                messageLabel.setText("Registration failed.");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            RealtimeDatabaseService.saveUserRole(email, role);

            messageLabel.setText("Registration successful! Please login.");
            messageLabel.setStyle("-fx-text-fill: green;");

            Router.goTo("login");

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void goToLogin() {
        Router.goTo("login");
    }
}
