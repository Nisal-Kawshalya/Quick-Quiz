package com.quizapp.controllers.dashboard;

import com.quizapp.models.Session;
import com.quizapp.routing.Router;
import javafx.fxml.FXML;

public class StudentDashboardController {

    @FXML
    private void goToAvailableQuizzes() {
        Router.goTo("available-quizzes");
    }

    @FXML
    private void handleLogout() {
        Session.clear();
        Router.goTo("login");
    }
}
