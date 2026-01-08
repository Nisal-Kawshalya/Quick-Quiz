package com.quizapp.controllers.quiz;

import com.quizapp.models.Question;
import com.quizapp.models.Quiz;
import com.quizapp.routing.Router;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.util.Duration;
import com.quizapp.models.QuizResultSession;


import java.util.ArrayList;
import java.util.List;

public class TakeQuizController {

    @FXML
    private Label questionLabel, timerLabel;
    
    @FXML
    private Label progressLabel;


    @FXML
    private ToggleGroup optionsGroup;

    @FXML
    private RadioButton option1, option2, option3, option4;

    private Quiz quiz;
    private int currentIndex = 0;
    private int score = 0;
    private int timeLeft;
    private Timeline timeline;

  @FXML
public void initialize() {

    // ✅ CREATE ToggleGroup manually
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


    private void loadQuiz() {
        List<Question> questions = new ArrayList<>();
        questions.add(new Question(
                "What is JVM?",
                List.of("Java Virtual Machine", "Java Variable Method", "JSON VM", "None"),
                0
        ));
        questions.add(new Question(
                "Java is?",
                List.of("OS", "Programming Language", "Browser", "Game"),
                1
        ));

        quiz = new Quiz("Java Basics", questions, 60);
        timeLeft = quiz.getTimeLimitSeconds();
    }

    private void startTimer() {
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

    private void showQuestion() {
        Question q = quiz.getQuestions().get(currentIndex);
        questionLabel.setText(q.getQuestionText());

        option1.setText(q.getOptions().get(0));
        option2.setText(q.getOptions().get(1));
        option3.setText(q.getOptions().get(2));
        option4.setText(q.getOptions().get(3));

        optionsGroup.selectToggle(null);
    }

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

    private void checkAnswer() {
        RadioButton selected = (RadioButton) optionsGroup.getSelectedToggle();
        if (selected == null) return;

        int selectedIndex = List.of(option1, option2, option3, option4).indexOf(selected);
        if (selectedIndex == quiz.getQuestions().get(currentIndex).getCorrectIndex()) {
            score++;
        }
    }

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
