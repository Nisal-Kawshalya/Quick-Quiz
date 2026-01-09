package com.quizapp.firebase;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class FirestoreService {

    private static final OkHttpClient client = new OkHttpClient();

    // 🔑 CONSISTENT USER DOCUMENT ID
    private static String userDocId(String email) {
        return email.trim()
                .replace(".", "_")
                .replace("@", "_");
    }

    // ===============================
    // SAVE USER ROLE (REGISTER)
    // ===============================
    public static void saveUserRole(String email, String role) throws Exception {

        String url = FirebaseConfig.FIRESTORE_BASE_URL +
                FirebaseConfig.PROJECT_ID +
                "/databases/(default)/documents/users/" +
                userDocId(email);

        JSONObject fields = new JSONObject();
        fields.put("role", new JSONObject().put("stringValue", role));

        JSONObject body = new JSONObject();
        body.put("fields", fields);

        Request request = new Request.Builder()
                .url(url)
                .patch(RequestBody.create(
                        body.toString(),
                        MediaType.get("application/json")))
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new RuntimeException(
                    "Failed to save role: " + response.body().string()
            );
        }
    }

    // ===============================
    // GET USER ROLE (LOGIN)
    // ===============================
    public static String getUserRole(String email) throws Exception {

        String url = FirebaseConfig.FIRESTORE_BASE_URL +
                FirebaseConfig.PROJECT_ID +
                "/databases/(default)/documents/users/" +
                userDocId(email);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new RuntimeException(
                    "User role not found in Firestore"
            );
        }

        String responseBody = response.body().string();
        JSONObject json = new JSONObject(responseBody);

        return json
                .getJSONObject("fields")
                .getJSONObject("role")
                .getString("stringValue");
    }

    // ===============================
    // SAVE QUIZ RESULT
    // ===============================
    public static void saveQuizResult(
            String email,
            String quizTitle,
            int score,
            int total
    ) throws Exception {

        String url = FirebaseConfig.FIRESTORE_BASE_URL +
                FirebaseConfig.PROJECT_ID +
                "/databases/(default)/documents/results";

        double percentage = ((double) score / total) * 100;

        JSONObject fields = new JSONObject();

        fields.put("email", new JSONObject().put("stringValue", email));
        fields.put("quizTitle", new JSONObject().put("stringValue", quizTitle));
        fields.put("score", new JSONObject().put("integerValue", score));
        fields.put("total", new JSONObject().put("integerValue", total));
        fields.put("percentage", new JSONObject().put("doubleValue", percentage));
        fields.put("timestamp",
                new JSONObject().put("timestampValue", Instant.now().toString()));

        JSONObject body = new JSONObject();
        body.put("fields", fields);

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(
                        body.toString(),
                        MediaType.get("application/json")))
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new RuntimeException("Failed to save quiz result");
        }
    }

    // ===============================
    // GET ALL QUIZ RESULTS
    // ===============================
    public static List<QuizResult> getAllQuizResults() throws Exception {

        String url = FirebaseConfig.FIRESTORE_BASE_URL +
                FirebaseConfig.PROJECT_ID +
                "/databases/(default)/documents/results";

        System.out.println("🔍 Fetching results from: " + url);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful() || response.body() == null) {
            System.out.println("❌ Failed to fetch results. Status: " + 
                    (response.isSuccessful() ? "OK" : response.code()));
            return new ArrayList<>();
        }

        String responseBody = response.body().string();
        System.out.println("📥 Response body length: " + responseBody.length());
        System.out.println("📥 Response preview: " + 
                (responseBody.length() > 200 ? responseBody.substring(0, 200) + "..." : responseBody));

        if (responseBody.equals("null") || responseBody.isEmpty()) {
            System.out.println("⚠️ No results found in database (null or empty)");
            return new ArrayList<>();
        }

        JSONObject json = new JSONObject(responseBody);
        List<QuizResult> results = new ArrayList<>();

        if (json.has("documents")) {
            JSONArray documents = json.getJSONArray("documents");
            System.out.println("📚 Found " + documents.length() + " documents");

            for (int i = 0; i < documents.length(); i++) {
                try {
                    JSONObject doc = documents.getJSONObject(i);
                    JSONObject fields = doc.getJSONObject("fields");

                    String email = fields.getJSONObject("email").getString("stringValue");
                    String quizTitle = fields.getJSONObject("quizTitle").getString("stringValue");
                    int score = fields.getJSONObject("score").getInt("integerValue");
                    int total = fields.getJSONObject("total").getInt("integerValue");
                    double percentage = fields.getJSONObject("percentage").getDouble("doubleValue");
                    String timestamp = fields.optJSONObject("timestamp") != null ?
                            fields.getJSONObject("timestamp").getString("timestampValue") : "";

                    QuizResult result = new QuizResult(email, quizTitle, score, total, percentage, timestamp);
                    results.add(result);
                    System.out.println("✅ Parsed result: " + email + " - " + quizTitle + " (" + score + "/" + total + ")");
                } catch (Exception e) {
                    System.err.println("❌ Error parsing document " + i + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("⚠️ Response does not contain 'documents' field. Response keys: " + json.keySet());
        }

        System.out.println("✅ Total results parsed: " + results.size());
        return results;
    }

    // ===============================
    // GET RESULTS BY STUDENT EMAIL
    // ===============================
    public static List<QuizResult> getResultsByStudent(String studentEmail) throws Exception {
        List<QuizResult> allResults = getAllQuizResults();
        List<QuizResult> studentResults = new ArrayList<>();

        for (QuizResult result : allResults) {
            if (result.getStudentEmail().equalsIgnoreCase(studentEmail)) {
                studentResults.add(result);
            }
        }

        return studentResults;
    }

    // ===============================
    // QUIZ RESULT MODEL
    // ===============================
    public static class QuizResult {
        private final String studentEmail;
        private final String quizTitle;
        private final int score;
        private final int total;
        private final double percentage;
        private final String timestamp;

        public QuizResult(String studentEmail, String quizTitle, int score, int total,
                         double percentage, String timestamp) {
            this.studentEmail = studentEmail;
            this.quizTitle = quizTitle;
            this.score = score;
            this.total = total;
            this.percentage = percentage;
            this.timestamp = timestamp;
        }

        public String getStudentEmail() { return studentEmail; }
        public String getQuizTitle() { return quizTitle; }
        public int getScore() { return score; }
        public int getTotal() { return total; }
        public double getPercentage() { return percentage; }
        public String getTimestamp() { return timestamp; }
    }
}
