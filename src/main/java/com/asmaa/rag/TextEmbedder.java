package com.asmaa.rag;

import java.util.List;

public class TextEmbedder {
    private RemoteEmbedder remoteEmbedder;

    public TextEmbedder() {
        // ممكن تغيري الـ URL لو بتشغلي API من مكان تاني
        this.remoteEmbedder = new RemoteEmbedder("http://127.0.0.1:5005/embed");
    }

    public float[] embed(String text) {
        try {
            List<Float> vector = remoteEmbedder.embed(text);
            float[] result = new float[vector.size()];
            for (int i = 0; i < vector.size(); i++) {
                result[i] = vector.get(i);
            }
            return result;
        } catch (Exception e) {
            System.err.println("Failed to get embedding: " + e.getMessage());
            return new float[0];
        }
    }

    public void printVector(float[] vector) {
        System.out.print("[");
        for (int i = 0; i < vector.length; i++) {
            System.out.print(vector[i]);
            if (i != vector.length - 1) System.out.print(", ");
        }
        System.out.println("]");
    }
}
