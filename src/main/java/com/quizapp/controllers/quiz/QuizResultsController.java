package com.quizapp.controllers.quiz;

import com.quizapp.firebase.FirestoreService;
import com.quizapp.models.Quiz;
import com.quizapp.models.QuizResultSession;
import com.quizapp.models.QuizSession;
import com.quizapp.models.Session;
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

        // Save result to Firestore (async to not block UI)
        new Thread(() -> {
            try {
                Quiz quiz = QuizSession.getQuiz();
                if (quiz != null && Session.getUser() != null) {
                    String studentEmail = Session.getUser().getEmail();
                    String quizTitle = quiz.getTitle();
                    
                    FirestoreService.saveQuizResult(studentEmail, quizTitle, score, total);
                    System.out.println("✅ Quiz result saved to Firestore");
                }
            } catch (Exception e) {
                System.err.println("❌ Failed to save quiz result: " + e.getMessage());
                e.printStackTrace();
                // Don't show error to user - result display should still work
            }
        }).start();
    }

    @FXML
    private void goBackToQuizzes() {
        QuizResultSession.clear();
        QuizSession.clear();
        Router.goTo("available-quizzes");
    }
}
