package com.asmaa.rag;

import java.util.List;

public class App {
    public static void main(String[] args) {
        TextFileLoader loader = new TextFileLoader();
        List<Document> documents = loader.loadDocuments();

        RemoteEmbedder embedder = new RemoteEmbedder("http://127.0.0.1:5005/embed");
        MilvusVectorStore vectorStore = new MilvusVectorStore("127.0.0.1", 19530); // Connect to Milvus

        for (Document doc : documents) {
            System.out.println(doc);

            List<Float> vector = embedder.embed(doc.getContent());
            System.out.println(vector);

            // Convert List<Float> to float[] for Milvus
            float[] floatArray = new float[vector.size()];
            for (int i = 0; i < vector.size(); i++) {
                floatArray[i] = vector.get(i);
            }

            vectorStore.insertEmbedding(floatArray); // Store in Milvus
        }

        vectorStore.close(); // Always close connection
    }
}
