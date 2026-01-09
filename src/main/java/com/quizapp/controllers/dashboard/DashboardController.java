package com.quizapp.controllers.dashboard;

import com.quizapp.models.Session;
import com.quizapp.routing.Router;
import javafx.fxml.FXML;

public class DashboardController {

    @FXML
    private void goToCreateQuiz() {
        Router.goTo("create-quiz");
    }

    @FXML
    private void handleLogout() {
        Session.clear();
        Router.goTo("login");
    }
}
