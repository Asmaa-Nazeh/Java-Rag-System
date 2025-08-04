package com.asmaa.rag;

import java.util.List;

public class EmbeddingIngestion {

    public static void main(String[] args) {
        // 1. تحميل الملفات النصية
        TextFileLoader loader = new TextFileLoader();
        List<Document> documents = loader.loadDocuments();

        // 2. تهيئة الـ Embedder
        TextEmbedder embedder = new TextEmbedder();

        // 3. الاتصال بـ Milvus
        MilvusVectorStore milvus = new MilvusVectorStore("localhost", 19530);

        // 4. إدخال الـ embeddings داخل Milvus
        for (Document doc : documents) {
            float[] vector = embedder.embed(doc.getContent());
            if (vector.length > 0) {
                milvus.insertEmbedding(vector);
            }
        }

        // 5. إغلاق الاتصال بـ Milvus
        milvus.close();
    }
}
