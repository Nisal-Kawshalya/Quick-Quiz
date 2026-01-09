package com.quizapp.models;

import java.util.List;

public class Quiz {

    // 🔑 Database ID
    private String id;

    // 🧑‍🏫 Teacher info
    private String teacherEmail;

    // 📘 Quiz info
    private String title;
    private String subject;
    private String difficulty;

    // ⏱ Quiz rules
    private int timeLimitSeconds;

    // ❓ Questions
    private List<Question> questions;

    // 🚦 Publish status
    private boolean published;

    // ===============================
    // CONSTRUCTOR (Student side)
    // ===============================
    public Quiz(String title, List<Question> questions, int timeLimitSeconds) {
        this.title = title;
        this.questions = questions;
        this.timeLimitSeconds = timeLimitSeconds;
        this.published = false;
    }

    // ===============================
    // CONSTRUCTOR (Teacher side)
    // ===============================
    public Quiz(String id,
                String teacherEmail,
                String title,
                String subject,
                String difficulty,
                int timeLimitSeconds,
                List<Question> questions) {

        this.id = id;
        this.teacherEmail = teacherEmail;
        this.title = title;
        this.subject = subject;
        this.difficulty = difficulty;
        this.timeLimitSeconds = timeLimitSeconds;
        this.questions = questions;
        this.published = false;
    }

    // ===============================
    // GETTERS
    // ===============================
    public String getId() {
        return id;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public String getTitle() {
        return title;
    }

    public String getSubject() {
        return subject;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public boolean isPublished() {
        return published;
    }

    // ===============================
    // SETTERS
    // ===============================
    public void setPublished(boolean published) {
        this.published = published;
    }
}
