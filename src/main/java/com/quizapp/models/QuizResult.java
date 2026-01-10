package com.quizapp.models;

public class QuizResult {

    private final String studentEmail;
    private final String quizId;
    private final int score;
    private final int total;
    private final double percentage;

    public QuizResult(
            String studentEmail,
            String quizId,
            int score,
            int total,
            double percentage
    ) {
        this.studentEmail = studentEmail;
        this.quizId = quizId;
        this.score = score;
        this.total = total;
        this.percentage = percentage;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getQuizId() {
        return quizId;
    }

    public int getScore() {
        return score;
    }

    public int getTotal() {
        return total;
    }

    public double getPercentage() {
        return percentage;
    }

    public boolean isPassed() {
        return percentage >= 50;
    }
}
