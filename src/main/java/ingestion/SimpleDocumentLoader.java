package ingestion;

import java.io.*;
import java.util.*;

public class SimpleDocumentLoader {

    public List<Map<String, String>> loadDocuments() {
        List<Map<String, String>> documents = new ArrayList<>();
        String[] files = { "doc1.txt" }; // الملفات الموجودة في resources

        for (String file : files) {
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(file)) {
                if (inputStream == null) continue;

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder contentBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    contentBuilder.append(line).append("\n");
                }

                Map<String, String> doc = new HashMap<>();
                doc.put("filename", file);
                doc.put("content", contentBuilder.toString());

                documents.add(doc);

            } catch (IOException e) {
                System.err.println("Error loading file: " + file);
                e.printStackTrace();
            }
        }

        return documents;
    }
}
