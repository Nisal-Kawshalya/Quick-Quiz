package com.quizapp.controllers.quiz;

import com.quizapp.models.Question;
import com.quizapp.models.Quiz;
import com.quizapp.models.QuizStore;
import com.quizapp.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.util.ArrayList;
import java.util.List;

public class CreateQuizController {

    // Quiz info
    @FXML
    private TextField titleField, timeField;

    // Question form
    @FXML
    private TextField questionField;
    @FXML
    private TextField option1Field, option2Field, option3Field, option4Field;

    @FXML
    private RadioButton correct1, correct2, correct3, correct4;

    @FXML
    private Label statusLabel;

    // Logic
    private ToggleGroup correctGroup;
    private final List<Question> questions = new ArrayList<>();

    // ===== INITIALIZE =====
    @FXML
    public void initialize() {
        correctGroup = new ToggleGroup();

        correct1.setToggleGroup(correctGroup);
        correct2.setToggleGroup(correctGroup);
        correct3.setToggleGroup(correctGroup);
        correct4.setToggleGroup(correctGroup);
    }

    // ===== ADD QUESTION =====
    @FXML
    private void addQuestion() {

        if (questionField.getText().isEmpty()
                || option1Field.getText().isEmpty()
                || option2Field.getText().isEmpty()
                || option3Field.getText().isEmpty()
                || option4Field.getText().isEmpty()
                || correctGroup.getSelectedToggle() == null) {

            statusLabel.setText("Please fill all fields and select correct answer.");
            return;
        }

        int correctIndex =
                List.of(correct1, correct2, correct3, correct4)
                        .indexOf(correctGroup.getSelectedToggle());

        Question question = new Question(
                questionField.getText(),
                List.of(
                        option1Field.getText(),
                        option2Field.getText(),
                        option3Field.getText(),
                        option4Field.getText()
                ),
                correctIndex
        );

        questions.add(question);

        clearQuestionForm();
        statusLabel.setText("Question added! Total: " + questions.size());
    }

    // ===== SAVE QUIZ =====
    @FXML
    private void saveQuiz() {

        if (titleField.getText().isEmpty()
                || timeField.getText().isEmpty()
                || questions.isEmpty()) {

            statusLabel.setText("Quiz title, time, and at least one question required.");
            return;
        }

        int time = Integer.parseInt(timeField.getText());

        Quiz quiz = new Quiz(
                titleField.getText(),
                questions,
                time
        );

        QuizStore.addQuiz(quiz);
        Router.goTo("dashboard");
    }

    // ===== CLEAR QUESTION FORM =====
    private void clearQuestionForm() {
        questionField.clear();
        option1Field.clear();
        option2Field.clear();
        option3Field.clear();
        option4Field.clear();
        correctGroup.selectToggle(null);
    }
}
