package com.rootgame.model.NPC;
/*
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NPCDescriptionGenerator {

    private static final String API_URL = "https://chatgpt-api.shn.hk/v1/";
    
    public static void generateNPCDescription(NPC npc) {
        try {
            OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        String jsonBody = "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"user\", \"content\": \"Hello, how are you?\"}]}";
        JSONObject json = new JSONObject();
        json.put("model", "gpt-3.5-turbo");
        json.put("messages", new JSONObject().put("role", "user").put("content", "Hello, how are you?"));

        System.out.println(json.toString());
        RequestBody requestBody = RequestBody.create(json.toString(), mediaType);

        Request request = new Request.Builder()
                .url("https://chatgpt-api.shn.hk/v1/")
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build();
    
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                System.out.println(responseBody);
            } else {
                System.out.println("Request failed with error code: " + response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String parseGeneratedDescription(String responseBody) {
        // Implement the parsing logic based on the OpenAI API response format
        // Extract the generated description from the response JSON and return it
        return ""; // Replace with your parsing logic
    }
}
*/


