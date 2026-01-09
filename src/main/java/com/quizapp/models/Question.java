package com.quizapp.models;

import java.util.List;

public class Question {

    // 🔑 For database reference (teacher side)
    private String id;

    // 📝 Question data
    private String questionText;
    private List<String> options;
    private int correctIndex;

    // ===============================
    // CONSTRUCTOR (Student side)
    // ===============================
    public Question(String questionText, List<String> options, int correctIndex) {
        this.questionText = questionText;
        this.options = options;
        this.correctIndex = correctIndex;
    }

    // ===============================
    // CONSTRUCTOR (Teacher side)
    // ===============================
    public Question(String id, String questionText,
                    List<String> options, int correctIndex) {
        this.id = id;
        this.questionText = questionText;
        this.options = options;
        this.correctIndex = correctIndex;
    }

    // ===============================
    // GETTERS
    // ===============================
    public String getId() {
        return id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }
}
