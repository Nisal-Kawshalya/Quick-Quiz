package com.quizapp.firebase;

import com.quizapp.models.QuizResult;
import okhttp3.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RealtimeDatabaseService {

    private static final OkHttpClient client = new OkHttpClient();

    // ===============================
    // USER KEY (EMAIL SAFE)
    // ===============================
    private static String userKey(String email) {
        return email.trim()
                .replace(".", "_")
                .replace("@", "_");
    }

    // ===============================
    // SAVE USER ROLE
    // ===============================
    public static void saveUserRole(String email, String role) throws Exception {

        String url = FirebaseConfig.REALTIME_DB_URL +
                "/users/" + userKey(email) + ".json";

        JSONObject body = new JSONObject();
        body.put("role", role);

        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create(
                        body.toString(),
                        MediaType.get("application/json")))
                .build();

        client.newCall(request).execute();
    }

    // ===============================
    // GET USER ROLE
    // ===============================
    public static String getUserRole(String email) throws Exception {

        String url = FirebaseConfig.REALTIME_DB_URL +
                "/users/" + userKey(email) + "/role.json";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string().replace("\"", "");
    }

    // ===============================
    // SAVE QUIZ RESULT (STUDENT)
    // ===============================
    public static void saveQuizResult(
            String quizId,
            String studentEmail,
            int score,
            int total
    ) throws Exception {

        double percentage = ((double) score / total) * 100;

        String url = FirebaseConfig.REALTIME_DB_URL +
                "/results/" + quizId + ".json";

        JSONObject result = new JSONObject();
        result.put("studentEmail", studentEmail);
        result.put("score", score);
        result.put("total", total);
        result.put("percentage", percentage);

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(
                        result.toString(),
                        MediaType.get("application/json")))
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new RuntimeException("Failed to save quiz result");
        }

        System.out.println("✅ Result saved to Realtime DB");
    }

    // ===============================
    // GET RESULTS FOR A QUIZ (TEACHER)
    // ===============================
    public static List<QuizResult> getResultsForQuiz(String quizId) throws Exception {

        List<QuizResult> results = new ArrayList<>();

        String url = FirebaseConfig.REALTIME_DB_URL +
                "/results/" + quizId + ".json";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful() || response.body() == null) {
            return results;
        }

        String body = response.body().string();
        if (body.equals("null")) return results;

        JSONObject json = new JSONObject(body);

        for (String key : json.keySet()) {

            JSONObject obj = json.getJSONObject(key);

            results.add(new QuizResult(
                    obj.getString("studentEmail"),
                    quizId,
                    obj.getInt("score"),
                    obj.getInt("total"),
                    obj.getDouble("percentage")
            ));
        }

        return results;
    }

    // ===============================
    // GET RESULTS BY STUDENT
    // ===============================
    public static List<QuizResult> getResultsByStudent(String studentEmail) throws Exception {

        List<QuizResult> results = new ArrayList<>();

        String url = FirebaseConfig.REALTIME_DB_URL + "/results.json";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful() || response.body() == null) {
            return results;
        }

        String body = response.body().string();
        if (body.equals("null")) return results;

        JSONObject quizzes = new JSONObject(body);

        for (String quizId : quizzes.keySet()) {

            JSONObject quizResults = quizzes.getJSONObject(quizId);

            for (String key : quizResults.keySet()) {

                JSONObject r = quizResults.getJSONObject(key);

                if (r.getString("studentEmail").equalsIgnoreCase(studentEmail)) {

                    results.add(new QuizResult(
                            studentEmail,
                            quizId,
                            r.getInt("score"),
                            r.getInt("total"),
                            r.getDouble("percentage")
                    ));
                }
            }
        }

        return results;
    }
}
