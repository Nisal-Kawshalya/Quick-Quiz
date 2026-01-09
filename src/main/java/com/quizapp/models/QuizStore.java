package com.quizapp.models;

import java.util.ArrayList;
import java.util.List;

public class QuizStore {

    private static final List<Quiz> quizzes = new ArrayList<>();

    public static void addQuiz(Quiz quiz) {
        quizzes.add(quiz);
    }

    public static List<Quiz> getQuizzes() {
        return quizzes;
    }
}
