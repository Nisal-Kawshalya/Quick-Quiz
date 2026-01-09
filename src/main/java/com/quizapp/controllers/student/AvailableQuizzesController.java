package com.quizapp.controllers.student;

import com.quizapp.models.Quiz;
import com.quizapp.models.QuizSession;
import com.quizapp.models.QuizStore;
import com.quizapp.models.Session;
import com.quizapp.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AvailableQuizzesController {

    @FXML
    private VBox quizList;

    @FXML
    public void initialize() {

        quizList.getChildren().clear();

        for (Quiz quiz : QuizStore.getQuizzes()) {

            Label title = new Label(quiz.getTitle());
            Button startBtn = new Button("Start Quiz");

            startBtn.setOnAction(e -> {
                QuizSession.setQuiz(quiz);
                Router.goTo("take-quiz");
            });

            VBox quizBox = new VBox(5, title, startBtn);
            quizBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc;");

            quizList.getChildren().add(quizBox);
        }

        if (QuizStore.getQuizzes().isEmpty()) {
            quizList.getChildren().add(
                    new Label("No quizzes available yet.")
            );
        }
    }
    @FXML
    public void startQuiz() {
        System.out.println("Student page loaded"); // 🔍 DEBUG
        Router.goTo("take-quiz");
    }


    @FXML
    public void handleLogout() {
        Session.clear();
        Router.goTo("login");
    }
}
