package com.quizapp.controllers.quiz;

import java.util.List;

import com.quizapp.firebase.RealtimeDatabaseService;
import com.quizapp.models.Question;
import com.quizapp.models.Quiz;
import com.quizapp.models.QuizResultSession;
import com.quizapp.models.QuizSession;
import com.quizapp.models.Session;
import com.quizapp.routing.Router;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class TakeQuizController {

    // ===== UI ELEMENTS =====
    @FXML
    private Label questionLabel;

    @FXML
    private Label timerLabel;

    @FXML
    private Label progressLabel;

    @FXML
    private VBox optionsBox;

    // ===== LOGIC =====
    private ToggleGroup optionsGroup;
    private Quiz quiz;
    private int currentIndex = 0;
    private int score = 0;
    private int timeLeft;
    private Timeline timeline;

    // ===== INITIALIZE =====
    @FXML
    public void initialize() {
        loadQuiz();
        startTimer();
        showQuestion();
    }

    // ===== LOAD QUIZ =====
    private void loadQuiz() {

        quiz = QuizSession.getQuiz();

        if (quiz == null || quiz.getQuestions() == null || quiz.getQuestions().isEmpty()) {
            System.err.println("❌ Quiz not loaded properly");
            Router.goTo("available-quizzes");
            return;
        }

        timeLeft = quiz.getTimeLimitSeconds();
    }

    // ===== TIMER =====
    private void startTimer() {

        timerLabel.setText("Time left: " + timeLeft + "s");

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    timeLeft--;
                    timerLabel.setText("Time left: " + timeLeft + "s");

                    if (timeLeft <= 0) {
                        submitQuiz();
                    }
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // ===== SHOW QUESTION =====
    private void showQuestion() {

        Question q = quiz.getQuestions().get(currentIndex);

        progressLabel.setText(
                "Question " + (currentIndex + 1) + " / " + quiz.getQuestions().size()
        );

        questionLabel.setText(q.getQuestionText());

        optionsBox.getChildren().clear();
        optionsGroup = new ToggleGroup();

        List<String> options = q.getOptions();

        for (int i = 0; i < options.size(); i++) {
            RadioButton rb = new RadioButton(options.get(i));
            rb.setToggleGroup(optionsGroup);
            rb.setUserData(i);
            optionsBox.getChildren().add(rb);
        }
    }

    // ===== NEXT QUESTION =====
    @FXML
    private void nextQuestion() {

        checkAnswer();
        currentIndex++;

        if (currentIndex < quiz.getQuestions().size()) {
            showQuestion();
        } else {
            submitQuiz();
        }
    }
@FXML
private void handleLogout() {

    // Stop timer safely
    if (timeline != null) {
        timeline.stop();
    }

    // Clear sessions
    Session.clear();
    QuizSession.clear();

    // Go to login
    Router.goTo("login");
}

    // ===== CHECK ANSWER =====
    private void checkAnswer() {

        if (optionsGroup.getSelectedToggle() == null) return;

        int selectedIndex =
                (int) optionsGroup.getSelectedToggle().getUserData();

        if (selectedIndex ==
                quiz.getQuestions().get(currentIndex).getCorrectIndex()) {
            score++;
        }
    }

    // ===== SUBMIT QUIZ =====
    private void submitQuiz() {

        if (timeline != null) timeline.stop();

        QuizResultSession.setResult(score, quiz.getQuestions().size());

        new Thread(() -> {
            try {
                RealtimeDatabaseService.saveQuizResult(
                        quiz.getId(),
                        Session.getUser().getEmail(),
                        score,
                        quiz.getQuestions().size()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        Router.goTo("quiz-result");
    }
}
