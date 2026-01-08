package com.quizapp.models;

public class User {

    private String email;
    private String role; // STUDENT or TEACHER

    public User(String email, String role) {
        this.email = email;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
