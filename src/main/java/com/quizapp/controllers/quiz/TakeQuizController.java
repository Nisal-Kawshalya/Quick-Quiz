package com.quizapp.controllers.quiz;

import com.quizapp.models.Question;
import com.quizapp.models.Quiz;
import com.quizapp.models.QuizResultSession;
import com.quizapp.routing.Router;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class TakeQuizController {

    // ===== UI ELEMENTS =====
    @FXML
    private Label questionLabel;

    @FXML
    private Label timerLabel;

    @FXML
    private Label progressLabel;

    @FXML
    private RadioButton option1, option2, option3, option4;

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

        // ✅ Create ToggleGroup manually
        optionsGroup = new ToggleGroup();

        // ✅ Attach ToggleGroup to RadioButtons
        option1.setToggleGroup(optionsGroup);
        option2.setToggleGroup(optionsGroup);
        option3.setToggleGroup(optionsGroup);
        option4.setToggleGroup(optionsGroup);

        loadQuiz();
        startTimer();
        showQuestion();
    }

    // ===== LOAD QUIZ =====
    private void loadQuiz() {

        List<Question> questions = new ArrayList<>();

        questions.add(new Question(
                "What is JVM?",
                List.of(
                        "Java Virtual Machine",
                        "Java Variable Method",
                        "JSON VM",
                        "None"
                ),
                0
        ));

        questions.add(new Question(
                "Java is?",
                List.of(
                        "OS",
                        "Programming Language",
                        "Browser",
                        "Game"
                ),
                1
        ));

        quiz = new Quiz("Java Basics", questions, 60);
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

        // Progress text
        progressLabel.setText(
                "Question " + (currentIndex + 1) + " / " + quiz.getQuestions().size()
        );

        questionLabel.setText(q.getQuestionText());

        option1.setText(q.getOptions().get(0));
        option2.setText(q.getOptions().get(1));
        option3.setText(q.getOptions().get(2));
        option4.setText(q.getOptions().get(3));

        // Clear selection
        optionsGroup.selectToggle(null);
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

    // ===== CHECK ANSWER =====
    private void checkAnswer() {

        RadioButton selected =
                (RadioButton) optionsGroup.getSelectedToggle();

        if (selected == null) return;

        int selectedIndex =
                List.of(option1, option2, option3, option4).indexOf(selected);

        if (selectedIndex ==
                quiz.getQuestions().get(currentIndex).getCorrectIndex()) {
            score++;
        }
    }

    // ===== SUBMIT QUIZ =====
    private void submitQuiz() {

        if (timeline != null) {
            timeline.stop();
        }

        QuizResultSession.setResult(
                score,
                quiz.getQuestions().size()
        );

        Router.goTo("quiz-result");
    }
}
