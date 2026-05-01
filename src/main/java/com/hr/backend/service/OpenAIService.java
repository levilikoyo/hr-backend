/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hr.backend.service;

/**
 *
 * @author apple
 */





import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class OpenAIService {

   private static final String API_KEY = System.getenv("OPENAI_API_KEY");
    public String askAI(String question) {
        try {
            OkHttpClient client = new OkHttpClient();

            JsonObject json = new JsonObject();
            json.addProperty("model", "gpt-4.1-mini");

            JsonObject message = new JsonObject();
            message.addProperty("role", "user");
            message.addProperty("content", question);

            JsonArray messages = new JsonArray();
            messages.add(message);

            json.add("messages", messages);

            RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    return response.body().string();
                } else {
                    return "Empty response body";
                }
            }

        } catch (Exception e) {
            return e.getMessage();
        }
    }
}