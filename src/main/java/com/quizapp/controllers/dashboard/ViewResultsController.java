package com.quizapp.controllers.dashboard;

import com.quizapp.firebase.RealtimeDatabaseService;
import com.quizapp.models.Quiz;
import com.quizapp.models.QuizResult;
import com.quizapp.models.QuizSession;
import com.quizapp.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;

import java.util.List;

public class ViewResultsController {

    @FXML
    private VBox resultsContainer;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    public void initialize() {

        resultsContainer.getChildren().clear();

        try {
            // Get selected quiz
            Quiz quiz = QuizSession.getQuiz();

            if (quiz == null) {
                resultsContainer.getChildren().add(
                        new Label("No quiz selected.")
                );
                return;
            }

            // ✅ Fetch results from Realtime Database
            List<QuizResult> results =
                    RealtimeDatabaseService.getResultsForQuiz(quiz.getId());

            Label header = new Label("📊 Results for: " + quiz.getTitle());
            header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            resultsContainer.getChildren().add(header);

            if (results.isEmpty()) {
                resultsContainer.getChildren().add(
                        new Label("No students have attempted this quiz yet.")
                );
                return;
            }

            for (QuizResult result : results) {

                Label studentLabel = new Label(
                        "👤 Student Email: " + result.getStudentEmail()
                );

                Label scoreLabel = new Label(
                        "📌 Score: " + result.getScore()
                                + " / " + result.getTotal()
                );

                Label percentageLabel = new Label(
                        String.format("📈 Percentage: %.2f%%",
                                result.getPercentage())
                );

                Label statusLabel = new Label(
                        result.isPassed() ? "PASS ✅" : "FAIL ❌"
                );

                VBox box = new VBox(
                        5,
                        studentLabel,
                        scoreLabel,
                        percentageLabel,
                        statusLabel
                );

                box.setStyle("""
                        -fx-padding: 15;
                        -fx-border-color: #ccc;
                        -fx-border-radius: 5;
                        -fx-background-color: #f9f9f9;
                        -fx-background-radius: 5;
                        """);

                resultsContainer.getChildren().add(box);
            }

        } catch (Exception e) {
            e.printStackTrace();
            resultsContainer.getChildren().add(
                    new Label("Failed to load results.")
            );
        }
    }

    @FXML
    private void goBack() {
        Router.goTo("teacher-dashboard");
    }
}
