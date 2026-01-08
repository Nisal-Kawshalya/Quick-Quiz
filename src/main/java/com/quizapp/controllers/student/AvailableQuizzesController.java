package com.quizapp.controllers.student;

import com.quizapp.routing.Router;
import com.quizapp.models.Session;
import javafx.fxml.FXML;

public class AvailableQuizzesController {

  @FXML
    private void startQuiz() {
        System.out.println("Start Quiz clicked"); 
        Router.goTo("take-quiz");
    }


    @FXML
    private void handleLogout() {
        Session.clear();
        Router.goTo("login");
    }
}
