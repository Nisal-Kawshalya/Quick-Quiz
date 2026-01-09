package com.quizapp.firebase;

import okhttp3.*;
import org.json.JSONObject;

public class RealtimeDatabaseService {

    private static final OkHttpClient client = new OkHttpClient();

    private static String userKey(String email) {
        return email.trim()
                .replace(".", "_")
                .replace("@", "_");
    }

    // SAVE ROLE
    public static void saveUserRole(String email, String role) throws Exception {

        String url = "https://" + FirebaseConfig.PROJECT_ID +
                "-default-rtdb.asia-southeast1.firebasedatabase.app/users/" +
                userKey(email) + ".json";

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

    // GET ROLE
    public static String getUserRole(String email) throws Exception {

        String url = "https://" + FirebaseConfig.PROJECT_ID +
                "-default-rtdb.asia-southeast1.firebasedatabase.app/users/" +
                userKey(email) + "/role.json";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string().replace("\"", "");
    }
}
