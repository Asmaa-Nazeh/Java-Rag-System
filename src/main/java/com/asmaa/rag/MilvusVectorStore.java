package com.asmaa.rag;

import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.param.*;
import io.milvus.param.collection.*;
import io.milvus.param.dml.*;
import io.milvus.param.index.CreateIndexParam;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MilvusVectorStore {

    private static final String COLLECTION_NAME = "text_embeddings";
    private static final String VECTOR_FIELD_NAME = "embedding";
    private static final String ID_FIELD_NAME = "id";

    private final MilvusServiceClient client;

    public MilvusVectorStore(String host, int port) {
        ConnectParam connectParam = ConnectParam.newBuilder()
                .withHost(host)
                .withPort(port)
                .withConnectTimeout(10, TimeUnit.SECONDS)
                .build();
        this.client = new MilvusServiceClient(connectParam);

        createCollectionIfNotExists();
    }

    private void createCollectionIfNotExists() {
        HasCollectionParam hasCollectionParam = HasCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build();

        R<Boolean> hasCollection = client.hasCollection(hasCollectionParam);
        if (!hasCollection.getData()) {
            System.out.println("Collection not found. Creating...");

            FieldType idField = FieldType.newBuilder()
                    .withName(ID_FIELD_NAME)
                    .withDataType(DataType.Int64)
                    .withPrimaryKey(true)
                    .withAutoID(true)
                    .build();

            FieldType vectorField = FieldType.newBuilder()
                    .withName(VECTOR_FIELD_NAME)
                    .withDataType(DataType.FloatVector)
                    .withDimension(384) // for MiniLM
                    .build();

            CreateCollectionParam createParam = CreateCollectionParam.newBuilder()
                    .withCollectionName(COLLECTION_NAME)
                    .withDescription("Text Embeddings Collection")
                    .withShardsNum(2)
                    .addFieldType(idField)
                    .addFieldType(vectorField)
                    .build();

            client.createCollection(createParam);
        }
    }

    public void insertEmbedding(float[] embedding) {
        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field(VECTOR_FIELD_NAME, Collections.singletonList(embedding)));

        InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withFields(fields)
                .build();

        client.insert(insertParam);
        System.out.println("Embedding inserted into Milvus.");
    }

    public void close() {
        client.close();
    }
}
