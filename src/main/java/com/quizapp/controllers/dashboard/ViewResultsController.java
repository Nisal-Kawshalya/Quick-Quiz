package com.quizapp.controllers.dashboard;

import java.util.List;

import com.quizapp.firebase.RealtimeDatabaseService;
import com.quizapp.models.Quiz;
import com.quizapp.models.QuizResult;
import com.quizapp.models.QuizSession;
import com.quizapp.routing.Router;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ViewResultsController {

    @FXML
    private VBox resultsContainer;

    @FXML
    public void initialize() {

        resultsContainer.getChildren().clear();

        try {
            // Get selected quiz
            Quiz quiz = QuizSession.getQuiz();

            if (quiz == null) {
                Label error = new Label("No quiz selected.");
                error.getStyleClass().add("empty-text");
                resultsContainer.getChildren().add(error);
                return;
            }

            // Header
            Label header = new Label("📊 Results for: " + quiz.getTitle());
            header.getStyleClass().add("result-header");
            resultsContainer.getChildren().add(header);

            // Fetch results
            List<QuizResult> results =
                    RealtimeDatabaseService.getResultsForQuiz(quiz.getId());

            if (results.isEmpty()) {
                Label empty = new Label("No students have attempted this quiz yet.");
                empty.getStyleClass().add("empty-text");
                resultsContainer.getChildren().add(empty);
                return;
            }

            // Build cards
            for (QuizResult result : results) {
                resultsContainer.getChildren().add(createResultCard(result));
            }

        } catch (Exception e) {
            e.printStackTrace();
            Label error = new Label("Failed to load results.");
            error.getStyleClass().add("error-text");
            resultsContainer.getChildren().add(error);
        }
    }

    /* ===============================
       RESULT CARD (MODERN)
    =============================== */
    private VBox createResultCard(QuizResult result) {

        VBox card = new VBox(8);
        card.getStyleClass().add("result-card");

        Label student = new Label("👤 Student Email: " + result.getStudentEmail());
        student.getStyleClass().add("result-text");

        Label score = new Label(
                "⭐ Score: " + result.getScore() + " / " + result.getTotal()
        );
        score.getStyleClass().add("result-text");

        Label percentage = new Label(
                String.format("📈 Percentage: %.2f%%", result.getPercentage())
        );
        percentage.getStyleClass().add("result-text");

        Label status = new Label(
                result.isPassed() ? "PASS ✓" : "FAIL ✗"
        );
        status.getStyleClass().add(
                result.isPassed() ? "pass-badge" : "fail-badge"
        );

        card.getChildren().addAll(student, score, percentage, status);
        return card;
    }

    @FXML
    private void goBack() {
        Router.goTo("teacher-dashboard");
    }
}
