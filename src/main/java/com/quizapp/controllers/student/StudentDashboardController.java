package com.quizapp.controllers.student;

import com.quizapp.models.Session;
import com.quizapp.routing.Router;

import javafx.fxml.FXML;

public class StudentDashboardController {

    // ===============================
    // NAVIGATION
    // ===============================
    @FXML
    private void goToAvailableQuizzes() {
        Router.goTo("available-quizzes");
    }

    @FXML
    private void goToMyResults() {
        try {
            System.out.println("📊 Navigating to student results...");
            Router.goTo("student-results");
        } catch (Exception e) {
            System.err.println("❌ Error navigating to results: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        Session.clear();
        Router.goTo("login");
    }
}
