package com.quizapp.firebase;

import okhttp3.*;
import org.json.JSONObject;

import java.util.UUID;

public class TeacherService {

    private static final OkHttpClient client = new OkHttpClient();

    private static String baseUrl() {
        return "https://" + FirebaseConfig.PROJECT_ID +
                "-default-rtdb.asia-southeast1.firebasedatabase.app/";
    }

    // ===============================
    // CREATE QUIZ
    // ===============================
    public static String createQuiz(
            String teacherEmail,
            String title,
            String subject,
            String difficulty
    ) throws Exception {

        String quizId = UUID.randomUUID().toString();

        JSONObject quizJson = new JSONObject();
        quizJson.put("title", title);
        quizJson.put("subject", subject);
        quizJson.put("difficulty", difficulty);
        quizJson.put("teacherEmail", teacherEmail);

        Request request = new Request.Builder()
                .url(baseUrl() + "quizzes/" + quizId + ".json")
                .put(RequestBody.create(
                        quizJson.toString(),
                        MediaType.get("application/json")))
                .build();

        client.newCall(request).execute();

        return quizId;
    }

    // ===============================
    // ADD QUESTION TO QUIZ
    // ===============================
    public static void addQuestion(
            String quizId,
            String questionText,
            String[] options,
            int correctIndex
    ) throws Exception {

        String questionId = UUID.randomUUID().toString();

        JSONObject q = new JSONObject();
        q.put("text", questionText);
        q.put("correctIndex", correctIndex);
        q.put("options", options);

        Request request = new Request.Builder()
                .url(baseUrl() + "quizzes/" + quizId +
                        "/questions/" + questionId + ".json")
                .put(RequestBody.create(
                        q.toString(),
                        MediaType.get("application/json")))
                .build();

        client.newCall(request).execute();
    }
}
