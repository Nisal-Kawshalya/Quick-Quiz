package com.quizapp.firebase;

import com.quizapp.models.Question;
import com.quizapp.models.Quiz;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StudentQuizService {

    private static final OkHttpClient client = new OkHttpClient();

    private static String baseUrl() {
        return "https://" + FirebaseConfig.PROJECT_ID +
                "-default-rtdb.asia-southeast1.firebasedatabase.app/";
    }

    // ===============================
    // FETCH ONLY PUBLISHED QUIZZES
    // ===============================
    public static List<Quiz> getPublishedQuizzes() throws Exception {

        List<Quiz> quizzes = new ArrayList<>();

        Request request = new Request.Builder()
                .url(baseUrl() + "quizzes.json")
                .get()
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful() || response.body() == null) {
            System.out.println("❌ Firebase response failed");
            return quizzes;
        }

        String responseBody = response.body().string();
        if (responseBody.equals("null")) {
            System.out.println("❌ No quizzes in database");
            return quizzes;
        }

        JSONObject json = new JSONObject(responseBody);
        Iterator<String> keys = json.keys();

        while (keys.hasNext()) {
            String quizId = keys.next();
            JSONObject quizJson = json.getJSONObject(quizId);

            // 🔍 DEBUG OUTPUT (IMPORTANT)
            System.out.println("Quiz: " + quizJson.toString());
            System.out.println("Published raw: " + quizJson.opt("published"));

            // ✅ SAFELY CHECK published
            boolean published = false;

            Object publishedObj = quizJson.opt("published");
            if (publishedObj instanceof Boolean) {
                published = (Boolean) publishedObj;
            }

            if (!published) {
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

            quiz.setPublished(true);
            quizzes.add(quiz);
        }

        System.out.println("✅ Published quizzes loaded: " + quizzes.size());
        return quizzes;
    }

    // ===============================
    // FETCH COMPLETE QUIZ BY ID (WITH QUESTIONS)
    // ===============================
    public static Quiz getQuizById(String quizId) throws Exception {

        Request request = new Request.Builder()
                .url(baseUrl() + "quizzes/" + quizId + ".json")
                .get()
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful() || response.body() == null) {
            System.out.println("❌ Failed to fetch quiz: " + quizId);
            return null;
        }

        String responseBody = response.body().string();
        if (responseBody.equals("null")) {
            System.out.println("❌ Quiz not found: " + quizId);
            return null;
        }

        JSONObject quizJson = new JSONObject(responseBody);

        // Parse questions
        List<Question> questions = new ArrayList<>();
        if (quizJson.has("questions")) {
            JSONObject questionsJson = quizJson.getJSONObject("questions");
            Iterator<String> questionKeys = questionsJson.keys();

            while (questionKeys.hasNext()) {
                String key = questionKeys.next();
                JSONObject qJson = questionsJson.getJSONObject(key);

                String questionText = qJson.getString("questionText");
                int correctIndex = qJson.getInt("correctIndex");
                JSONArray optionsArray = qJson.getJSONArray("options");

                List<String> options = new ArrayList<>();
                for (int i = 0; i < optionsArray.length(); i++) {
                    options.add(optionsArray.getString(i));
                }

                Question question = new Question(questionText, options, correctIndex);
                questions.add(question);
            }
        }

        // Create quiz object
        Quiz quiz = new Quiz(
                quizId,
                quizJson.getString("teacherEmail"),
                quizJson.getString("title"),
                quizJson.getString("subject"),
                quizJson.getString("difficulty"),
                quizJson.getInt("timeLimitSeconds"),
                questions
        );

        quiz.setPublished(quizJson.optBoolean("published", false));

        System.out.println("✅ Quiz loaded: " + quiz.getTitle() + " with " + questions.size() + " questions");
        return quiz;
    }
}
