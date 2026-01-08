package com.quizapp.models;

import java.util.List;

public class Quiz {

    private String title;
    private List<Question> questions;
    private int timeLimitSeconds;

    public Quiz(String title, List<Question> questions, int timeLimitSeconds) {
        this.title = title;
        this.questions = questions;
        this.timeLimitSeconds = timeLimitSeconds;
    }

    public String getTitle() {
        return title;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public int getTimeLimitSeconds() {
        return timeLimitSeconds;
    }
}
