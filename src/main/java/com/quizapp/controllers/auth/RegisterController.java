package com.quizapp.controllers.auth;

import com.quizapp.firebase.FirebaseAuthService;
import com.quizapp.firebase.RealtimeDatabaseService;
import com.quizapp.routing.Router;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleBox;
    @FXML private Label messageLabel;

    // 🔹 Center card (for animation)
    @FXML private VBox registerCard;

    @FXML
    public void initialize() {
        // Populate roles
        roleBox.getItems().addAll("STUDENT", "TEACHER");

        // Play intro animation
        playIntroAnimation();
    }

    // 🔹 Fade + Slide animation
    private void playIntroAnimation() {
        registerCard.setOpacity(0);
        registerCard.setTranslateY(30);

        FadeTransition fade = new FadeTransition(Duration.millis(600), registerCard);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(600), registerCard);
        slide.setFromY(30);
        slide.setToY(0);

        fade.play();
        slide.play();
    }

    // 🔹 Register logic (UNCHANGED)
    @FXML
    private void handleRegister() {

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String role = roleBox.getValue();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            showMessage("Please fill in all fields.", false);
            return;
        }

        try {
            boolean registered = FirebaseAuthService.register(email, password);

            if (!registered) {
                showMessage("Registration failed.", false);
                return;
            }

            RealtimeDatabaseService.saveUserRole(email, role);

            showMessage("Registration successful! Please login.", true);

            Router.goTo("login");

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Error: " + e.getMessage(), false);
        }
    }

    // 🔹 Navigation
    @FXML
    private void goToLogin() {
        Router.goTo("login");
    }

    // 🔹 Helper method for messages
    private void showMessage(String text, boolean success) {
        messageLabel.setText(text);
        messageLabel.setStyle(
                success
                        ? "-fx-text-fill: #27ae60;"
                        : "-fx-text-fill: #e74c3c;"
        );
    }
}
