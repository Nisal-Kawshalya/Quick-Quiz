package com.quizapp.routing;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class Router {

    private static BorderPane root;

    public static void setRoot(BorderPane rootPane) {
        root = rootPane;
    }

    public static void goTo(String page) {
        try {
            Parent view;

            switch (page) {

                case "login":
                    view = FXMLLoader.load(
                            Router.class.getResource("/fxml/auth/LoginView.fxml")
                    );
                    break;

                case "register":
                    view = FXMLLoader.load(
                            Router.class.getResource("/fxml/auth/RegisterView.fxml")
                    );
                    break;

                case "student-dashboard":
                    view = FXMLLoader.load(
                            Router.class.getResource("/fxml/student/AvailableQuizzesView.fxml")
                    );
                    break;

                case "available-quizzes":
                    view = FXMLLoader.load(
                            Router.class.getResource("/fxml/student/AvailableQuizzesView.fxml")
                    );
                    break;

                case "teacher-dashboard":
                    view = FXMLLoader.load(
                            Router.class.getResource("/fxml/dashboard/TeacherDashboardView.fxml")
                    );
                    break;

                case "create-quiz":
                    view = FXMLLoader.load(
                            Router.class.getResource("/fxml/quiz/CreateQuizView.fxml")
                    );
                    break;

                case "take-quiz":
                    view = FXMLLoader.load(
                            Router.class.getResource("/fxml/quiz/TakeQuizView.fxml")
                    );
                    break;

                case "quiz-result":
                    view = FXMLLoader.load(
                            Router.class.getResource("/fxml/quiz/QuizResultsView.fxml")
                    );
                    break;

                case "view-results":
                    view = FXMLLoader.load(
                            Router.class.getResource("/fxml/dashboard/ViewResultsView.fxml")
                    );
                    break;

                case "student-results":
                    view = FXMLLoader.load(
                            Router.class.getResource("/fxml/student/StudentResultsView.fxml")
                    );
                    break;

                default:
                    throw new IllegalArgumentException("Unknown page: " + page);
            }

            root.setCenter(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
