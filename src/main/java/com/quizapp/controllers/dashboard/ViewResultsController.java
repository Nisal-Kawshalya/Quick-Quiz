package com.quizapp.controllers.dashboard;

import com.quizapp.firebase.FirestoreService;
import com.quizapp.firebase.TeacherQuizService;
import com.quizapp.models.Quiz;
import com.quizapp.models.Session;
import com.quizapp.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ViewResultsController {

    @FXML
    private VBox resultsContainer;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    public void initialize() {
        resultsContainer.getChildren().clear();

        try {
            String teacherEmail = Session.getUser().getEmail();
            System.out.println("🔍 Teacher email: " + teacherEmail);

            // Get all quizzes created by this teacher
            List<Quiz> teacherQuizzes = TeacherQuizService.getQuizzesByTeacher(teacherEmail);
            System.out.println("📚 Teacher's quizzes count: " + teacherQuizzes.size());
            
            Set<String> teacherQuizTitles = new HashSet<>();
            for (Quiz quiz : teacherQuizzes) {
                teacherQuizTitles.add(quiz.getTitle());
                System.out.println("  - Quiz: " + quiz.getTitle());
            }

            // Get all quiz results
            List<FirestoreService.QuizResult> allResults = FirestoreService.getAllQuizResults();
            System.out.println("📊 Total results in database: " + allResults.size());

            if (allResults.isEmpty()) {
                resultsContainer.getChildren().add(
                        new Label("No quiz results available yet.")
                );
                return;
            }

            // Filter results for this teacher's quizzes
            boolean hasResults = false;
            for (FirestoreService.QuizResult result : allResults) {
                System.out.println("🔍 Checking result - Quiz: " + result.getQuizTitle() + 
                                 ", Student: " + result.getStudentEmail());
                
                if (teacherQuizTitles.contains(result.getQuizTitle())) {
                    hasResults = true;
                    System.out.println("✅ Match found! Adding result for quiz: " + result.getQuizTitle());

                    Label quizTitleLabel = new Label("📝 Quiz: " + result.getQuizTitle());
                    quizTitleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                    Label studentLabel = new Label("Student: " + result.getStudentEmail());
                    Label scoreLabel = new Label(
                            "Score: " + result.getScore() + " / " + result.getTotal()
                    );
                    Label percentageLabel = new Label(
                            String.format("Percentage: %.2f%%", result.getPercentage())
                    );

                    String status = result.getPercentage() >= 50 ? "✅ PASS" : "❌ FAIL";
                    Label statusLabel = new Label("Status: " + status);
                    statusLabel.setStyle("-fx-font-weight: bold;");

                    VBox resultBox = new VBox(5, quizTitleLabel, studentLabel, scoreLabel, percentageLabel, statusLabel);
                    resultBox.setStyle("""
                            -fx-padding: 15;
                            -fx-border-color: #ccc;
                            -fx-border-radius: 5;
                            -fx-background-color: #f9f9f9;
                            -fx-background-radius: 5;
                            -fx-spacing: 5;
                            """);

                    resultsContainer.getChildren().add(resultBox);
                }
            }

            if (!hasResults) {
                resultsContainer.getChildren().add(
                        new Label("No results for your quizzes yet.")
                );
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
        Router.goTo("teacher-dashboard");
    }
}
