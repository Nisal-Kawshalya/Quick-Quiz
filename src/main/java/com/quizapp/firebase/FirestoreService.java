package com.quizapp.firebase;

import okhttp3.*;
import org.json.JSONObject;

import java.time.Instant;

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
}
