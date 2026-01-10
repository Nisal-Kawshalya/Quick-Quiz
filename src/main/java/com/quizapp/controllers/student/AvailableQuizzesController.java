package com.quizapp.controllers.student;

import com.quizapp.firebase.RealtimeDatabaseService;
import com.quizapp.firebase.StudentQuizService;
import com.quizapp.models.Quiz;
import com.quizapp.models.QuizResult;
import com.quizapp.models.QuizSession;
import com.quizapp.models.Session;
import com.quizapp.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AvailableQuizzesController {

    @FXML
    private VBox quizList;

    @FXML
    public void initialize() {

        quizList.getChildren().clear();

        try {
            String studentEmail = Session.getUser().getEmail();

            // ✅ GET COMPLETED QUIZZES (BY QUIZ ID)List<QuizResult> completed =
            List<QuizResult> completed =
            RealtimeDatabaseService.getResultsByStudent(studentEmail);
            Set<String> completedQuizIds = new HashSet<>();
            for (QuizResult r : completed) {
                completedQuizIds.add(r.getQuizId());
        }


            // ✅ GET ALL PUBLISHED QUIZZES
            List<Quiz> publishedQuizzes =
                    StudentQuizService.getPublishedQuizzes();

            if (publishedQuizzes.isEmpty()) {
                quizList.getChildren().add(
                        new Label("No quizzes available yet.")
                );
                return;
            }

            // ===============================
            // SHOW QUIZZES
            // ===============================
            for (Quiz quiz : publishedQuizzes) {

                boolean isCompleted =
                        completedQuizIds.contains(quiz.getId());

                Label title = new Label("📘 " + quiz.getTitle());
                title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                Label teacherLabel =
                        new Label("Teacher: " + quiz.getTeacherEmail());

                Label subjectLabel =
                        new Label("Subject: " + quiz.getSubject());

                Label difficultyLabel =
                        new Label("Difficulty: " + quiz.getDifficulty());

                Label timeLabel =
                        new Label("Time Limit: " + quiz.getTimeLimitSeconds() + " seconds");

                Button actionButton;

                if (isCompleted) {
                    // ❌ ALREADY ATTEMPTED
                    actionButton = new Button("Completed ✓");
                    actionButton.setDisable(true);
                    actionButton.setStyle(
                            "-fx-background-color: #9E9E9E; -fx-text-fill: white;"
                    );
                } else {
                    // ▶️ CAN TAKE QUIZ
                    actionButton = new Button("Take Quiz");
                    actionButton.setStyle(
                            "-fx-background-color: #4CAF50; -fx-text-fill: white;"
                    );

                    actionButton.setOnAction(e -> {
                        try {
                            Quiz fullQuiz =
                                    StudentQuizService.getQuizById(quiz.getId());

                            if (fullQuiz != null) {
                                QuizSession.setQuiz(fullQuiz);
                                Router.goTo("take-quiz");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }

                VBox quizInfo = new VBox(
                        5,
                        title,
                        teacherLabel,
                        subjectLabel,
                        difficultyLabel,
                        timeLabel
                );

                quizInfo.setStyle("""
                        -fx-padding: 15;
                        -fx-border-color: #ccc;
                        -fx-background-color: #f9f9f9;
                        -fx-border-radius: 5;
                        """);

                HBox quizBox = new HBox(15, quizInfo, actionButton);
                quizBox.setStyle("-fx-padding: 10;");

                quizList.getChildren().add(quizBox);
            }

        } catch (Exception e) {
            e.printStackTrace();
            quizList.getChildren().add(
                    new Label("Failed to load quizzes.")
            );
        }
    }

    @FXML
    public void handleLogout() {
        Session.clear();
        Router.goTo("login");
    }
}
