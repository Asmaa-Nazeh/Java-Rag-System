package com.asmaa.rag;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TextFileLoader {

    public List<Document> loadDocuments() {
        List<Document> documents = new ArrayList<>();

        try {
            ClassLoader classLoader = getClass().getClassLoader();
            String[] files = {
                    "doc1.txt"

            };

            for (String file : files) {
                InputStream inputStream = classLoader.getResourceAsStream(file);
                if (inputStream == null) continue;

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder contentBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    contentBuilder.append(line).append("\n");
                }

                documents.add(new Document(file, contentBuilder.toString()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return documents;
    }
}
