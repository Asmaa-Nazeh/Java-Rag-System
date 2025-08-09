package ingestion;

import java.util.*;
import com.google.gson.Gson;

public class App {
    public static void main(String[] args) {
        try {
            SimpleDocumentLoader loader = new SimpleDocumentLoader();
            List<Map<String, String>> documents = loader.loadDocuments();
            Gson gson = new Gson();

            RemoteEmbedder embedder = new RemoteEmbedder("http://127.0.0.1:5005/embed");
            MilvusVectorStore vectorStore = new MilvusVectorStore("127.0.0.1", 19530);

            for (Map<String, String> doc : documents) {
                String content = doc.get("content");
                String[] lines = content.split("\\r?\\n");
                int lineNumber = 0;

                for (String line : lines) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    List<Float> vector = embedder.embed(line);
                    float[] floatArray = new float[vector.size()];
                    for (int i = 0; i < vector.size(); i++) {
                        floatArray[i] = vector.get(i);
                    }

                    Map<String, Object> meta = new HashMap<>();
                    meta.put("line_number", ++lineNumber);
                    meta.put("timestamp", System.currentTimeMillis());

                    vectorStore.insertTextWithEmbedding(line, floatArray, gson.toJson(meta));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
