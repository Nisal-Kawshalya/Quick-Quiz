package com.quizapp.controllers.dashboard;

import com.quizapp.firebase.TeacherQuizService;
import com.quizapp.models.Quiz;
import com.quizapp.models.Session;
import com.quizapp.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class TeacherDashboardController {

    @FXML
    private VBox quizList;

    private List<Quiz> myQuizzes;

    // ===============================
    // LOAD TEACHER QUIZZES
    // ===============================
    @FXML
    public void initialize() {

        quizList.getChildren().clear();

        try {
            String teacherEmail = Session.getUser().getEmail();
            myQuizzes = TeacherQuizService.getQuizzesByTeacher(teacherEmail);

            if (myQuizzes.isEmpty()) {
                quizList.getChildren().add(
                        new Label("No quizzes created yet.")
                );
                return;
            }

            for (Quiz quiz : myQuizzes) {

                Label title = new Label("📘 " + quiz.getTitle());
                Label info = new Label(
                        "Difficulty: " + quiz.getDifficulty()
                                + " | Time: " + quiz.getTimeLimitSeconds() + "s"
                );

                Button publishBtn = new Button(
                        quiz.isPublished() ? "Published" : "Publish"
                );

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

                HBox actions = new HBox(10, publishBtn);

                VBox quizBox = new VBox(5, title, info, actions);
                quizBox.setStyle("""
                        -fx-padding: 10;
                        -fx-border-color: #ccc;
                        -fx-background-color: #f9f9f9;
                        """);

                quizList.getChildren().add(quizBox);
            }

        } catch (Exception e) {
            e.printStackTrace();
            quizList.getChildren().add(
                    new Label("Failed to load quizzes.")
            );
        }
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
            if (response.getText().equals("OK")) {
                Session.clear();
                Router.goTo("login");
            }
        });
    }
}
