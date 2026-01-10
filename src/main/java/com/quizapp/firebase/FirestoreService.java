package com.quizapp.firebase;

import okhttp3.*;
import org.json.JSONObject;

public class FirestoreService {

    private static final OkHttpClient client = new OkHttpClient();

    private static String userDocId(String email) {
        return email.trim()
                .replace(".", "_")
                .replace("@", "_");
    }

    // ===============================
    // SAVE USER ROLE
    // ===============================
    public static void saveUserRole(String email, String role) throws Exception {

        String url = FirebaseConfig.FIRESTORE_BASE_URL
                + FirebaseConfig.PROJECT_ID
                + "/databases/(default)/documents/users/"
                + userDocId(email);

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

        client.newCall(request).execute();
    }

    // ===============================
    // GET USER ROLE
    // ===============================
    public static String getUserRole(String email) throws Exception {

        String url = FirebaseConfig.FIRESTORE_BASE_URL
                + FirebaseConfig.PROJECT_ID
                + "/databases/(default)/documents/users/"
                + userDocId(email);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = client.newCall(request).execute();

        JSONObject json = new JSONObject(response.body().string());
        return json.getJSONObject("fields")
                .getJSONObject("role")
                .getString("stringValue");
    }
}
