package com.quizapp.firebase;

import com.quizapp.models.Question;
import com.quizapp.models.Quiz;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;


import java.util.UUID;

public class TeacherQuizService {

    private static final OkHttpClient client = new OkHttpClient();

    private static String baseUrl() {
        return "https://" + FirebaseConfig.PROJECT_ID +
                "-default-rtdb.asia-southeast1.firebasedatabase.app/";
    }

    // ===============================
    // SAVE QUIZ WITH QUESTIONS
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

        // QUESTIONS
        JSONObject questionsJson = new JSONObject();
        int index = 1;

        for (Question q : quiz.getQuestions()) {
            JSONObject qJson = new JSONObject();
            qJson.put("questionText", q.getQuestionText());
            qJson.put("correctIndex", q.getCorrectIndex());

            JSONArray optionsArray = new JSONArray(q.getOptions());
            qJson.put("options", optionsArray);

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
}
