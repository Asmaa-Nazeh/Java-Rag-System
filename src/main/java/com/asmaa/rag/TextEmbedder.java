package com.asmaa.rag;

import java.util.*;

public class TextEmbedder {

    private final Map<String, Integer> vocabulary = new HashMap<>();

    public float[] embed(String text) {
        String[] words = text.toLowerCase().replaceAll("[^a-zA-Z ]", "").split("\\s+");

        for (String word : words) {
            vocabulary.putIfAbsent(word, vocabulary.size());
        }

        float[] vector = new float[vocabulary.size()];
        for (String word : words) {
            int index = vocabulary.get(word);
            vector[index] += 1.0f;
        }

        return vector;
    }

    public void printVector(float[] vector) {
        System.out.print("Vector: [");
        for (int i = 0; i < vector.length; i++) {
            System.out.print(vector[i]);
            if (i < vector.length - 1) System.out.print(", ");
        }
        System.out.println("]");
    }
}
