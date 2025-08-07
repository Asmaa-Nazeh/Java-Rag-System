package com.asmaa.rag;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.grpc.DescribeIndexResponse;
import io.milvus.grpc.MutationResult;
import io.milvus.param.*;
import io.milvus.param.collection.*;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.param.index.DescribeIndexParam;

import java.util.*;

public class MilvusVectorStore {

    private static final String COLLECTION_NAME = "text_embeddings2";
    private static final String VECTOR_FIELD_NAME = "embedding";
    private static final String TEXT_FIELD_NAME = "text";
    private static final String METADATA_FIELD_NAME = "metadata";
    private static final String ID_FIELD_NAME = "id";

    private final MilvusConnection milvusConnection;
    private final MilvusServiceClient client;

    public MilvusVectorStore(String host, int port) {
        this.milvusConnection = MilvusConnection.getInstance(host, port, 10);
        this.client = milvusConnection.getClient();

        createCollectionAndIndexIfNotExists();
        loadCollection();
    }


    private void createCollectionAndIndexIfNotExists() {
        R<Boolean> hasCollection = client.hasCollection(
                HasCollectionParam.newBuilder().withCollectionName(COLLECTION_NAME).build()
        );

        if (!hasCollection.getData()) {
            System.out.println("Collection not found. Creating...");

            FieldType idField = FieldType.newBuilder()
                    .withName(ID_FIELD_NAME)
                    .withDataType(DataType.Int64)
                    .withPrimaryKey(true)
                    .withAutoID(true) // AutoID
                    .build();

            FieldType textField = FieldType.newBuilder()
                    .withName(TEXT_FIELD_NAME)
                    .withDataType(DataType.VarChar)
                    .withMaxLength(1024)
                    .build();

            FieldType metaField = FieldType.newBuilder()
                    .withName(METADATA_FIELD_NAME)
                    .withDataType(DataType.JSON)
                    .withMaxLength(2048)
                    .build();

            FieldType vectorField = FieldType.newBuilder()
                    .withName(VECTOR_FIELD_NAME)
                    .withDataType(DataType.FloatVector)
                    .withDimension(384)
                    .build();

            client.createCollection(CreateCollectionParam.newBuilder()
                    .withCollectionName(COLLECTION_NAME)
                    .withDescription("Text + Metadata Embeddings Collection")
                    .withShardsNum(2)
                    .addFieldType(idField)
                    .addFieldType(textField)
                    .addFieldType(metaField)
                    .addFieldType(vectorField)
                    .build()
            );
            System.out.println("Collection with metadata created.");
        }

        // Create index if not exists
        R<DescribeIndexResponse> indexInfo = client.describeIndex(
                DescribeIndexParam.newBuilder()
                        .withCollectionName(COLLECTION_NAME)
                        .withFieldName(VECTOR_FIELD_NAME)
                        .build()
        );

        if (indexInfo.getData() == null || indexInfo.getData().getIndexDescriptionsCount() == 0) {
            System.out.println("No index found. Creating index on vector field...");
            client.createIndex(CreateIndexParam.newBuilder()
                    .withCollectionName(COLLECTION_NAME)
                    .withFieldName(VECTOR_FIELD_NAME)
                    .withIndexType(IndexType.IVF_FLAT)
                    .withMetricType(MetricType.COSINE)
                    .withExtraParam("{\"nlist\":128}")
                    .withSyncMode(true)
                    .build()
            );
            System.out.println("Index created successfully.");
        }
    }

    private void loadCollection() {
        System.out.println("Loading collection into memory...");
        client.loadCollection(LoadCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build()
        );
        System.out.println("Collection loaded into memory.");
    }

    /**
     * Insert with JsonObject metadata
     */
    public void insertTextWithEmbedding(String text, float[] embedding, String metadataJson) {
        JsonObject metadataJsonObject = JsonParser.parseString(metadataJson).getAsJsonObject();
        List<Float> embeddingList = new ArrayList<>(embedding.length);
        for (float v : embedding) embeddingList.add(v);

        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field(TEXT_FIELD_NAME, Collections.singletonList(text)));
        fields.add(new InsertParam.Field(METADATA_FIELD_NAME, Collections.singletonList(metadataJsonObject)));
        fields.add(new InsertParam.Field(VECTOR_FIELD_NAME, Collections.singletonList(embeddingList)));

        R<MutationResult> result = client.insert(InsertParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withFields(fields)
                .build()
        );

        if (result.getStatus() == R.Status.Success.getCode()) {
            List<Long> ids = result.getData().getIDs().getIntId().getDataList();
            System.out.println("Inserted: " + text + " | meta: " + metadataJson + " | ID: " + ids);
        } else {
            System.err.println("Insert failed: " + result.getMessage());
        }
    }
}
