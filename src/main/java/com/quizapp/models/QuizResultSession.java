package com.quizapp.models;

public class QuizResultSession {

    private static int score;
    private static int totalQuestions;

    public static void setResult(int scoreValue, int total) {
        score = scoreValue;
        totalQuestions = total;
    }

    public static int getScore() {
        return score;
    }

    public static int getTotalQuestions() {
        return totalQuestions;
    }

    public static void clear() {
        score = 0;
        totalQuestions = 0;
    }
}
