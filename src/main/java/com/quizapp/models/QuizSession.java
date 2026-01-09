package com.quizapp.models;

public class QuizSession {

    private static Quiz selectedQuiz;

    public static void setQuiz(Quiz quiz) {
        selectedQuiz = quiz;
    }

    public static Quiz getQuiz() {
        return selectedQuiz;
    }

    public static void clear() {
        selectedQuiz = null;
    }
}
