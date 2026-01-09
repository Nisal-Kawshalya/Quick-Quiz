package com.quizapp.firebase;

import okhttp3.*;
import org.json.JSONObject;

public class FirebaseAuthService {

    private static final OkHttpClient client = new OkHttpClient();

    public static boolean register(String email, String password) throws Exception {

        String url = FirebaseConfig.AUTH_BASE_URL +
                ":signUp?key=" + FirebaseConfig.API_KEY;

        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("password", password);
        json.put("returnSecureToken", true);

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(
                        json.toString(),
                        MediaType.get("application/json")))
                .build();

        Response response = client.newCall(request).execute();
        return response.isSuccessful();
    }

  public static boolean login(String email, String password) {

    try {
        String url = FirebaseConfig.AUTH_BASE_URL +
                ":signInWithPassword?key=" + FirebaseConfig.API_KEY;

        JSONObject json = new JSONObject();
        json.put("email", email.trim());   // VERY IMPORTANT
        json.put("password", password);
        json.put("returnSecureToken", true);

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(
                        json.toString(),
                        MediaType.get("application/json")))
                .build();

        Response response = client.newCall(request).execute();

        String responseBody = response.body().string();
        System.out.println("LOGIN RESPONSE CODE: " + response.code());
        System.out.println("LOGIN RESPONSE BODY: " + responseBody);

        return response.isSuccessful();

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}


}
