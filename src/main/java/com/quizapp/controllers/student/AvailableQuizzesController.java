package com.quizapp.controllers.student;

import com.quizapp.firebase.FirestoreService;
import com.quizapp.firebase.StudentQuizService;
import com.quizapp.models.Quiz;
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
            
            // Get all quizzes completed by this student
            List<FirestoreService.QuizResult> studentResults = 
                    FirestoreService.getResultsByStudent(studentEmail);
            Set<String> completedQuizTitles = new HashSet<>();
            for (FirestoreService.QuizResult result : studentResults) {
                completedQuizTitles.add(result.getQuizTitle());
            }

            List<Quiz> publishedQuizzes = StudentQuizService.getPublishedQuizzes();

            if (publishedQuizzes.isEmpty()) {
                quizList.getChildren().add(
                        new Label("No quizzes available yet.")
                );
                return;
            }

            for (Quiz quiz : publishedQuizzes) {
                boolean isCompleted = completedQuizTitles.contains(quiz.getTitle());

                Label title = new Label("📘 " + quiz.getTitle());
                title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                Label teacherLabel = new Label("Teacher: " + quiz.getTeacherEmail());
                Label subjectLabel = new Label("Subject: " + quiz.getSubject());
                Label difficultyLabel = new Label("Difficulty: " + quiz.getDifficulty());
                Label timeLabel = new Label("Time Limit: " + quiz.getTimeLimitSeconds() + " seconds");

                Button startBtn;
                if (isCompleted) {
                    // Quiz already completed
                    startBtn = new Button("Completed ✓");
                    startBtn.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-padding: 8 16;");
                    startBtn.setDisable(true);
                    startBtn.setOnAction(null);
                } else {
                    // Quiz not completed yet
                    startBtn = new Button("Take Quiz");
                    startBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 16;");

                    startBtn.setOnAction(e -> {
                        try {
                            // Fetch complete quiz with questions
                            Quiz completeQuiz = StudentQuizService.getQuizById(quiz.getId());
                            if (completeQuiz != null && completeQuiz.getQuestions() != null) {
                                QuizSession.setQuiz(completeQuiz);
                                Router.goTo("take-quiz");
                            } else {
                                System.err.println("Failed to load quiz questions");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            System.err.println("Error loading quiz: " + ex.getMessage());
                        }
                    });
                }

                VBox quizInfo = new VBox(5, title, teacherLabel, subjectLabel, difficultyLabel, timeLabel);
                quizInfo.setStyle("""
                        -fx-padding: 15;
                        -fx-border-color: #ccc;
                        -fx-border-radius: 5;
                        -fx-background-color: #f9f9f9;
                        -fx-background-radius: 5;
                        """);

                HBox quizBox = new HBox(15, quizInfo, startBtn);
                quizBox.setStyle("-fx-alignment: center-left; -fx-padding: 10;");

                quizList.getChildren().add(quizBox);
            }

        } catch (Exception e) {
            e.printStackTrace();
            quizList.getChildren().add(
                    new Label("Failed to load quizzes. Please try again.")
            );
        }
    }

    @FXML
    public void handleLogout() {
        Session.clear();
        Router.goTo("login");
    }
}
