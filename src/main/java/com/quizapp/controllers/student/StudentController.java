package com.quizapp.controllers.student;

import com.quizapp.models.Session;
import com.quizapp.routing.Router;
import javafx.fxml.FXML;

public class StudentController {

    @FXML
    private void handleLogout() {
        Session.clear();
        Router.goTo("login");
    }
}
