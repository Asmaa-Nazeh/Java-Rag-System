package com.asmaa.rag;

import java.util.List;

public class App {
    public static void main(String[] args) {
        TextFileLoader loader = new TextFileLoader();
        List<Document> documents = loader.loadDocuments();

        TextEmbedder embedder = new TextEmbedder();

        for (Document doc : documents) {
            System.out.println(doc);
            float[] vector = embedder.embed(doc.getContent());
            embedder.printVector(vector);
        }
    }
}
