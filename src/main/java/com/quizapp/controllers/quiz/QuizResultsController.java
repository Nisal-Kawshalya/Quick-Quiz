package com.quizapp.controllers.quiz;

import com.quizapp.models.QuizResultSession;
import com.quizapp.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class QuizResultsController {

    @FXML
    private Label scoreLabel;

    @FXML
    private Label percentageLabel;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {

        int score = QuizResultSession.getScore();
        int total = QuizResultSession.getTotalQuestions();

        double percentage = ((double) score / total) * 100;

        scoreLabel.setText("Your Score: " + score + " / " + total);
        percentageLabel.setText(String.format("Percentage: %.2f%%", percentage));

        if (percentage >= 50) {
            statusLabel.setText("Status: PASS ✅");
            statusLabel.getStyleClass().setAll("success");
        } else {
            statusLabel.setText("Status: FAIL ❌");
            statusLabel.getStyleClass().setAll("error");
        }
    }

    @FXML
    private void retryQuiz() {
        QuizResultSession.clear();
        Router.goTo("take-quiz");
    }

    @FXML
    private void goBackToQuizzes() {
        QuizResultSession.clear();
        Router.goTo("student");
    }
}
