package com.quizapp.controllers.student;

import com.quizapp.firebase.RealtimeDatabaseService;
import com.quizapp.models.QuizResult;
import com.quizapp.models.Session;
import com.quizapp.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class StudentResultsController {

    @FXML
    private VBox resultsContainer;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    public void initialize() {

        resultsContainer.getChildren().clear();

        try {
            String studentEmail = Session.getUser().getEmail();
            System.out.println("📊 Loading results for student: " + studentEmail);

            // ✅ Correct model usage
            List<QuizResult> studentResults =
            RealtimeDatabaseService.getResultsByStudent(studentEmail);

            if (studentResults.isEmpty()) {
                resultsContainer.getChildren().add(
                        new Label("No quiz results yet. Complete some quizzes to see your results here.")
                );
                return;
            }

            for (QuizResult result : studentResults) {

                Label quizTitleLabel =
                        new Label("📝 Quiz: " + result.getQuizId());
                quizTitleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                Label scoreLabel =
                        new Label("Score: " + result.getScore() + " / " + result.getTotal());

                Label percentageLabel =
                        new Label(String.format("Percentage: %.2f%%", result.getPercentage()));

                String status = result.isPassed() ? "✅ PASS" : "❌ FAIL";
                Label statusLabel =
                        new Label("Status: " + status);
                statusLabel.setStyle("-fx-font-weight: bold;");

                VBox resultBox = new VBox(
                        5,
                        quizTitleLabel,
                        scoreLabel,
                        percentageLabel,
                        statusLabel
                );

                resultBox.setStyle("""
                        -fx-padding: 15;
                        -fx-border-color: #ccc;
                        -fx-background-color: #f9f9f9;
                        -fx-border-radius: 5;
                        """);

                resultsContainer.getChildren().add(resultBox);
            }

        } catch (Exception e) {
            e.printStackTrace();
            resultsContainer.getChildren().add(
                    new Label("Failed to load results. Please try again.")
            );
        }
    }

    @FXML
    private void goBack() {
        Router.goTo("available-quizzes");
    }
}
