package com.asmaa.rag;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class RemoteEmbedder {
    private final String endpoint;
    private final Gson gson = new Gson();

    // ✅ ده الـ constructor اللي محتاجينه
    public RemoteEmbedder(String endpoint) {
        this.endpoint = endpoint;
    }

    // ✅ دي الدالة embed اللي محتاجينها
    public List<Float> embed(String text) {
        try {
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            JsonObject jsonInput = new JsonObject();
            jsonInput.addProperty("text", text);
            String jsonString = gson.toJson(jsonInput);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonString.getBytes());
                os.flush();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JsonObject response = gson.fromJson(in, JsonObject.class);
            in.close();

            Type listType = new TypeToken<List<Float>>(){}.getType();
            return gson.fromJson(response.get("embedding"), listType);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
