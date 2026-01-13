package com.quizapp.controllers.auth;

import com.quizapp.firebase.FirebaseAuthService;
import com.quizapp.firebase.RealtimeDatabaseService;
import com.quizapp.models.Session;
import com.quizapp.models.User;
import com.quizapp.routing.Router;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    // 🔹 This is the CENTER CARD (VBox)
    @FXML private VBox loginCard;

    // 🔹 Runs automatically when FXML loads
    @FXML
    public void initialize() {
        playIntroAnimation();
    }

    // 🔹 Fade + Slide animation
    private void playIntroAnimation() {
        loginCard.setOpacity(0);
        loginCard.setTranslateY(30);

        FadeTransition fade = new FadeTransition(Duration.millis(600), loginCard);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(600), loginCard);
        slide.setFromY(30);
        slide.setToY(0);

        fade.play();
        slide.play();
    }

    // 🔹 LOGIN LOGIC (UNCHANGED)
    @FXML
    private void handleLogin() {

        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        try {
            boolean success = FirebaseAuthService.login(email, password);

            if (!success) {
                errorLabel.setText("Invalid email or password.");
                return;
            }

            String role = RealtimeDatabaseService.getUserRole(email);

            Session.setUser(new User(email, role));

            if (role.equalsIgnoreCase("TEACHER")) {
                Router.goTo("teacher-dashboard");
            } else if (role.equalsIgnoreCase("STUDENT")) {
                Router.goTo("student-dashboard");
            } else {
                errorLabel.setText("Unknown role.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Login failed. Check Firestore data.");
        }
    }

    // 🔹 Navigation
    @FXML
    private void goToRegister() {
        Router.goTo("register");
    }
}
