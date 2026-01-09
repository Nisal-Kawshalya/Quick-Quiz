package com.quizapp.controllers.quiz;

import com.quizapp.firebase.TeacherQuizService;
import com.quizapp.models.Question;
import com.quizapp.models.Quiz;
import com.quizapp.models.Session;
import com.quizapp.routing.Router;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class CreateQuizController {

    // ===============================
    // QUIZ INFO
    // ===============================
    @FXML
    private TextField titleField;

    @FXML
    private TextField timeField;

    // ===============================
    // QUESTION FORM
    // ===============================
    @FXML
    private TextField questionField;

    @FXML
    private TextField option1Field, option2Field, option3Field, option4Field;

    @FXML
    private RadioButton correct1, correct2, correct3, correct4;

    @FXML
    private Label statusLabel;

    @FXML
    private VBox questionsList;

    // ===============================
    // LOGIC
    // ===============================
    private ToggleGroup correctGroup;
    private final List<Question> questions = new ArrayList<>();
    private Integer editingIndex = null; // Index of question being edited, null if adding new

    // ===============================
    // INITIALIZE
    // ===============================
    @FXML
    public void initialize() {

        correctGroup = new ToggleGroup();

        correct1.setToggleGroup(correctGroup);
        correct2.setToggleGroup(correctGroup);
        correct3.setToggleGroup(correctGroup);
        correct4.setToggleGroup(correctGroup);

        refreshQuestionsList();
    }

    // ===============================
    // ADD QUESTION
    // ===============================
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

        int correctIndex = List.of(
                correct1, correct2, correct3, correct4
        ).indexOf(correctGroup.getSelectedToggle());

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

        if (editingIndex != null) {
            // Update existing question
            questions.set(editingIndex, question);
            editingIndex = null;
            statusLabel.setText("Question updated! Total: " + questions.size());
        } else {
            // Add new question
            questions.add(question);
            statusLabel.setText("Question added! Total: " + questions.size());
        }

        clearQuestionForm();
        refreshQuestionsList();
    }

    // ===============================
    // SAVE QUIZ (TEACHER → FIREBASE)
    // ===============================
    @FXML
    private void saveQuiz() {

        if (titleField.getText().isEmpty()
                || timeField.getText().isEmpty()
                || questions.isEmpty()) {

            statusLabel.setText(
                    "Quiz title, time, and at least one question required."
            );
            return;
        }

        try {
            int timeLimit = Integer.parseInt(timeField.getText());

            // 🔐 Logged-in teacher email
            String teacherEmail = Session.getUser().getEmail();

            // 🧠 Build Quiz object (teacher-side constructor)
            Quiz quiz = new Quiz(
                    null,                 // id (generated in service)
                    teacherEmail,
                    titleField.getText(),
                    "General",             // subject (can enhance later)
                    "Medium",              // difficulty (can enhance later)
                    timeLimit,
                    questions
            );

            // 💾 SAVE TO FIREBASE
            TeacherQuizService.saveQuiz(quiz);

            statusLabel.setText("Quiz saved successfully!");
            Router.goTo("teacher-dashboard");

        } catch (NumberFormatException e) {
            statusLabel.setText("Time must be a number.");
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Failed to save quiz.");
        }
    }

    // ===============================
    // REFRESH QUESTIONS LIST
    // ===============================
    private void refreshQuestionsList() {
        questionsList.getChildren().clear();

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            int index = i;

            Label questionNum = new Label("Question " + (i + 1) + ":");
            questionNum.setStyle("-fx-font-weight: bold;");

            Label questionText = new Label(q.getQuestionText());
            questionText.setWrapText(true);

            Label optionsLabel = new Label(
                    "Options: " + String.join(", ", q.getOptions())
            );
            optionsLabel.setWrapText(true);

            Label correctLabel = new Label(
                    "Correct Answer: Option " + (q.getCorrectIndex() + 1)
            );

            Button editBtn = new Button("Edit");
            editBtn.setOnAction(e -> editQuestion(index));

            Button deleteBtn = new Button("Delete");
            deleteBtn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
            deleteBtn.setOnAction(e -> deleteQuestion(index));

            HBox buttonsBox = new HBox(10, editBtn, deleteBtn);

            VBox questionBox = new VBox(5, questionNum, questionText, optionsLabel, correctLabel, buttonsBox);
            questionBox.setStyle("""
                    -fx-padding: 10;
                    -fx-border-color: #ccc;
                    -fx-border-radius: 5;
                    -fx-background-color: #f9f9f9;
                    -fx-background-radius: 5;
                    """);

            questionsList.getChildren().add(questionBox);
        }

        if (questions.isEmpty()) {
            Label noQuestionsLabel = new Label("No questions added yet.");
            questionsList.getChildren().add(noQuestionsLabel);
        }
    }

    // ===============================
    // EDIT QUESTION
    // ===============================
    private void editQuestion(int index) {
        Question q = questions.get(index);

        questionField.setText(q.getQuestionText());
        option1Field.setText(q.getOptions().get(0));
        option2Field.setText(q.getOptions().get(1));
        option3Field.setText(q.getOptions().get(2));
        option4Field.setText(q.getOptions().get(3));

        // Set correct answer
        RadioButton[] radioButtons = {correct1, correct2, correct3, correct4};
        correctGroup.selectToggle(radioButtons[q.getCorrectIndex()]);

        editingIndex = index;
        statusLabel.setText("Editing question " + (index + 1) + ". Modify and click 'Add Question' to save changes.");
    }

    // ===============================
    // DELETE QUESTION
    // ===============================
    private void deleteQuestion(int index) {
        questions.remove(index);
        refreshQuestionsList();
        statusLabel.setText("Question deleted. Total: " + questions.size());

        if (editingIndex != null && editingIndex == index) {
            editingIndex = null;
            clearQuestionForm();
        } else if (editingIndex != null && editingIndex > index) {
            editingIndex--; // Adjust editing index if needed
        }
    }

    // ===============================
    // CLEAR QUESTION FORM
    // ===============================
    private void clearQuestionForm() {
        questionField.clear();
        option1Field.clear();
        option2Field.clear();
        option3Field.clear();
        option4Field.clear();
        correctGroup.selectToggle(null);
        editingIndex = null;
    }
}
