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

            // Completed quizzes
            List<QuizResult> completed =
                    RealtimeDatabaseService.getResultsByStudent(studentEmail);

            Set<String> completedIds = new HashSet<>();
            for (QuizResult r : completed) {
                completedIds.add(r.getQuizId());
            }

            // Published quizzes
            List<Quiz> quizzes =
                    StudentQuizService.getPublishedQuizzes();

            if (quizzes.isEmpty()) {
                Label empty = new Label("No quizzes available yet.");
                empty.getStyleClass().add("empty-text");
                quizList.getChildren().add(empty);
                return;
            }

            for (Quiz quiz : quizzes) {
                addQuizCard(quiz, completedIds.contains(quiz.getId()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addQuizCard(Quiz quiz, boolean completed) {

        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("quiz-card");

        VBox info = new VBox(6);
        info.getStyleClass().add("quiz-info");
        HBox.setHgrow(info, Priority.ALWAYS);

        Label title = new Label(quiz.getTitle());
        title.getStyleClass().add("quiz-title");

        Label teacher = new Label("Teacher: " + quiz.getTeacherEmail());
        Label subject = new Label("Subject: " + quiz.getSubject());

        teacher.getStyleClass().add("quiz-meta");
        subject.getStyleClass().add("quiz-meta");

        HBox badges = new HBox(8);
        Label diff = new Label(quiz.getDifficulty());
        Label time = new Label(quiz.getTimeLimitSeconds() + "s");

        diff.getStyleClass().add("badge");
        time.getStyleClass().add("badge");

        badges.getChildren().addAll(diff, time);

        info.getChildren().addAll(title, teacher, subject, badges);

        Button actionBtn;

        if (completed) {
            actionBtn = new Button("Completed");
            actionBtn.setDisable(true);
            actionBtn.getStyleClass().add("completed-btn");
        } else {
            actionBtn = new Button("Take Quiz");
            actionBtn.getStyleClass().add("take-btn");

            actionBtn.setOnAction(e -> {
                try {
                    Quiz fullQuiz =
                            StudentQuizService.getQuizById(quiz.getId());
                    QuizSession.setQuiz(fullQuiz);
                    Router.goTo("take-quiz");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }

        card.getChildren().addAll(info, actionBtn);
        quizList.getChildren().add(card);
    }

    @FXML
    private void handleLogout() {
        Session.clear();
        QuizSession.clear();
        Router.goTo("login");
    }
}
