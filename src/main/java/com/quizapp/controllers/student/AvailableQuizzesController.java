package com.quizapp.controllers.student;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.quizapp.firebase.RealtimeDatabaseService;
import com.quizapp.firebase.StudentQuizService;
import com.quizapp.models.Quiz;
import com.quizapp.models.QuizResult;
import com.quizapp.models.QuizSession;
import com.quizapp.models.Session;
import com.quizapp.routing.Router;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class AvailableQuizzesController {

    @FXML
    private VBox quizList;

    @FXML
    public void initialize() {

        quizList.getChildren().clear();

        try {
            String studentEmail = Session.getUser().getEmail();

            /* ===============================
               GET COMPLETED QUIZZES
            =============================== */
            List<QuizResult> completedResults =
                    RealtimeDatabaseService.getResultsByStudent(studentEmail);

            Set<String> completedQuizIds = new HashSet<>();
            for (QuizResult result : completedResults) {
                completedQuizIds.add(result.getQuizId());
            }

            /* ===============================
               GET PUBLISHED QUIZZES
            =============================== */
            List<Quiz> publishedQuizzes =
                    StudentQuizService.getPublishedQuizzes();

            if (publishedQuizzes.isEmpty()) {
                Label empty = new Label("No quizzes available yet.");
                empty.getStyleClass().add("empty-text");
                quizList.getChildren().add(empty);
                return;
            }

            /* ===============================
               DISPLAY QUIZZES
            =============================== */
            for (Quiz quiz : publishedQuizzes) {
                addQuizCard(quiz, completedQuizIds.contains(quiz.getId()));
            }

        } catch (Exception e) {
            e.printStackTrace();
            quizList.getChildren().add(
                    new Label("Failed to load quizzes.")
            );
        }
    }

    /* ===============================
       QUIZ CARD
    =============================== */
    private void addQuizCard(Quiz quiz, boolean isCompleted) {

        // Card container
        HBox quizCard = new HBox(20);
        quizCard.setAlignment(Pos.CENTER_LEFT);
        quizCard.getStyleClass().add("quiz-card");

        // Left info box
        VBox infoBox = new VBox(6);
        infoBox.getStyleClass().add("quiz-info");
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        // Title
        Label title = new Label(quiz.getTitle());
        title.getStyleClass().add("quiz-title");

        // Teacher
        Label teacher = new Label("Teacher: " + quiz.getTeacherEmail());
        teacher.getStyleClass().add("quiz-meta");

        // Subject
        Label subject = new Label("Subject: " + quiz.getSubject());
        subject.getStyleClass().add("quiz-meta");

        // Meta badges
        HBox metaRow = new HBox(8);

        Label difficulty = new Label(quiz.getDifficulty());
        difficulty.getStyleClass().add("badge");

        Label time = new Label(quiz.getTimeLimitSeconds() + "s");
        time.getStyleClass().add("badge");

        metaRow.getChildren().addAll(difficulty, time);

        infoBox.getChildren().addAll(title, teacher, subject, metaRow);

        // Action button
        Button actionBtn;

        if (isCompleted) {
            actionBtn = new Button("Completed ✓");
            actionBtn.setDisable(true);
            actionBtn.getStyleClass().add("completed-btn");
        } else {
            actionBtn = new Button("Take Quiz");
            actionBtn.getStyleClass().add("take-btn");

            actionBtn.setOnAction(e -> {
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

        quizCard.getChildren().addAll(infoBox, actionBtn);
        quizList.getChildren().add(quizCard);
    }

    @FXML
    private void handleLogout() {
        Session.clear();
        Router.goTo("login");
    }
}
