package com.quizapp.firebase;

import com.quizapp.models.Question;
import com.quizapp.models.Quiz;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class TeacherQuizService {

    private static final OkHttpClient client = new OkHttpClient();

    private static String baseUrl() {
        return "https://" + FirebaseConfig.PROJECT_ID +
                "-default-rtdb.asia-southeast1.firebasedatabase.app/";
    }

    // ===============================
    // SAVE QUIZ
    // ===============================
    public static void saveQuiz(Quiz quiz) throws Exception {

        String quizId = UUID.randomUUID().toString();

        JSONObject quizJson = new JSONObject();
        quizJson.put("id", quizId);
        quizJson.put("teacherEmail", quiz.getTeacherEmail());
        quizJson.put("title", quiz.getTitle());
        quizJson.put("subject", quiz.getSubject());
        quizJson.put("difficulty", quiz.getDifficulty());
        quizJson.put("timeLimitSeconds", quiz.getTimeLimitSeconds());
        quizJson.put("published", false);

        JSONObject questionsJson = new JSONObject();
        int index = 1;

        for (Question q : quiz.getQuestions()) {
            JSONObject qJson = new JSONObject();
            qJson.put("questionText", q.getQuestionText());
            qJson.put("correctIndex", q.getCorrectIndex());
            qJson.put("options", new JSONArray(q.getOptions()));
            questionsJson.put("q" + index++, qJson);
        }

        quizJson.put("questions", questionsJson);

        Request request = new Request.Builder()
                .url(baseUrl() + "quizzes/" + quizId + ".json")
                .put(RequestBody.create(
                        quizJson.toString(),
                        MediaType.get("application/json")))
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new RuntimeException("Failed to save quiz");
        }
    }

    // ===============================
    // PUBLISH QUIZ
    // ===============================
    public static void publishQuiz(String quizId) throws Exception {

        JSONObject body = new JSONObject();
        body.put("published", true);

        Request request = new Request.Builder()
                .url(baseUrl() + "quizzes/" + quizId + ".json")
                .patch(RequestBody.create(
                        body.toString(),
                        MediaType.get("application/json")))
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new RuntimeException("Failed to publish quiz");
        }
    }

    // ===============================
    // FETCH QUIZZES BY TEACHER
    // ===============================
    public static List<Quiz> getQuizzesByTeacher(String teacherEmail) throws Exception {

        List<Quiz> quizzes = new ArrayList<>();

        Request request = new Request.Builder()
                .url(baseUrl() + "quizzes.json")
                .get()
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful() || response.body() == null) {
            return quizzes;
        }

        String responseBody = response.body().string();
        if (responseBody.equals("null")) {
            return quizzes;
        }

        JSONObject json = new JSONObject(responseBody);
        Iterator<String> keys = json.keys();

        while (keys.hasNext()) {
            String quizId = keys.next();
            JSONObject quizJson = json.getJSONObject(quizId);

            if (!quizJson.getString("teacherEmail")
                    .equalsIgnoreCase(teacherEmail)) {
                continue;
            }

            Quiz quiz = new Quiz(
                    quizId,
                    quizJson.getString("teacherEmail"),
                    quizJson.getString("title"),
                    quizJson.getString("subject"),
                    quizJson.getString("difficulty"),
                    quizJson.getInt("timeLimitSeconds"),
                    null
            );

            quiz.setPublished(quizJson.optBoolean("published", false));
            quizzes.add(quiz);
        }

        return quizzes;
    }
}
