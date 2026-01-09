package com.quizapp.controllers.auth;

import com.quizapp.firebase.FirebaseAuthService;
import com.quizapp.firebase.RealtimeDatabaseService;
import com.quizapp.models.Session;
import com.quizapp.models.User;
import com.quizapp.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() {

        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        try {
            boolean success =
                    FirebaseAuthService.login(email, password);

            if (!success) {
                errorLabel.setText("Invalid email or password.");
                return;
            }

            String role = RealtimeDatabaseService.getUserRole(email);


            Session.setUser(new User(email, role));

            if (role.equalsIgnoreCase("TEACHER")) {
                Router.goTo("teacher");
            } else if (role.equalsIgnoreCase("STUDENT")) {
                Router.goTo("student");
            } else {
                errorLabel.setText("Unknown role.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Login failed. Check Firestore data.");
        }
    }

    @FXML
    private void goToRegister() {
        Router.goTo("register");
    }
}
