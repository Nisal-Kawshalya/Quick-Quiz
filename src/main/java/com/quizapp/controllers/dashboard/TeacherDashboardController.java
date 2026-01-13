package com.quizapp.controllers.dashboard;

import java.util.List;

import com.quizapp.firebase.TeacherQuizService;
import com.quizapp.models.Quiz;
import com.quizapp.models.QuizSession;
import com.quizapp.models.Session;
import com.quizapp.routing.Router;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TeacherDashboardController {

    @FXML
    private VBox quizList;

    private List<Quiz> myQuizzes;

    // ===============================
    // INITIALIZE & LOAD QUIZZES
    // ===============================
    @FXML
    public void initialize() {

        quizList.getChildren().clear();

        try {
            String teacherEmail = Session.getUser().getEmail();
            myQuizzes = TeacherQuizService.getQuizzesByTeacher(teacherEmail);

            if (myQuizzes == null || myQuizzes.isEmpty()) {
                Label empty = new Label("No quizzes created yet.");
                empty.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
                quizList.getChildren().add(empty);
                return;
            }

            for (Quiz quiz : myQuizzes) {
                addQuizCard(quiz);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Label error = new Label("Failed to load quizzes.");
            error.setStyle("-fx-text-fill: red;");
            quizList.getChildren().add(error);
        }
    }

    // ===============================
    // CREATE A SINGLE QUIZ CARD
    // ===============================
    private void addQuizCard(Quiz quiz) {

        /* ===== OUTER CARD ===== */
        HBox quizCard = new HBox(20);
        quizCard.getStyleClass().add("quiz-card");
        quizCard.setAlignment(Pos.CENTER_LEFT);

        /* ===== LEFT INFO ===== */
        VBox infoBox = new VBox(6);
        infoBox.getStyleClass().add("quiz-info");

        Label title = new Label(quiz.getTitle());
        title.getStyleClass().add("quiz-title");

        Label meta = new Label(
                "Difficulty: " + quiz.getDifficulty()
                        + " | Time: " + quiz.getTimeLimitSeconds() + "s"
        );
        meta.getStyleClass().add("quiz-meta");

        infoBox.getChildren().addAll(title, meta);

        /* ===== RIGHT ACTIONS ===== */
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button publishBtn = new Button(
                quiz.isPublished() ? "Published" : "Publish"
        );
        publishBtn.getStyleClass().add("publish-btn");
        publishBtn.setDisable(quiz.isPublished());

        publishBtn.setOnAction(e -> {
            try {
                TeacherQuizService.publishQuiz(quiz.getId());
                publishBtn.setText("Published");
                publishBtn.setDisable(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button resultsBtn = new Button("Results");
        resultsBtn.getStyleClass().add("results-btn");
        resultsBtn.setDisable(!quiz.isPublished());

        resultsBtn.setOnAction(e -> {
            QuizSession.setQuiz(quiz);
            Router.goTo("view-results");
        });

        actions.getChildren().addAll(publishBtn, resultsBtn);

        /* ===== COMBINE ===== */
        quizCard.getChildren().addAll(infoBox, actions);
        quizList.getChildren().add(quizCard);
    }

    // ===============================
    // NAVIGATION
    // ===============================
    @FXML
    private void goToCreateQuiz() {
        Router.goTo("create-quiz");
    }

    // ===============================
    // LOGOUT WITH CONFIRMATION
    // ===============================
    @FXML
    private void handleLogout() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("Your session will be cleared.");

        alert.showAndWait().ifPresent(response -> {
            if (response.getText().equalsIgnoreCase("OK")) {
                Session.clear();
                Router.goTo("login");
            }
        });
    }
}
