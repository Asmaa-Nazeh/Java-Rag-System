package retrieval;

import com.asmaa.rag.RemoteEmbedder;
import com.asmaa.rag.MilvusConnection;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.SearchResults;
import io.milvus.param.R;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.MetricType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VectorRetriever {

    private static final String COLLECTION_NAME = "text_embeddings2";
    private static final String VECTOR_FIELD_NAME = "embedding";
    private static final String TEXT_FIELD_NAME = "text";

    private final MilvusConnection milvusConnection;
    private final MilvusServiceClient client;
    private final RemoteEmbedder embedder;

    public VectorRetriever() {
        // Use centralized connection management
        this.milvusConnection = MilvusConnection.getInstance();
        this.client = milvusConnection.getClient();

        this.embedder = new RemoteEmbedder("http://127.0.0.1:5005/embed");

        // Ensure collection is loaded
        loadCollection();
        System.out.println("VectorRetriever initialized with shared Milvus connection.");
    }

    private void loadCollection() {
        try {
            client.loadCollection(LoadCollectionParam.newBuilder()
                    .withCollectionName(COLLECTION_NAME)
                    .build()
            );
            System.out.println("Collection loaded into memory for retrieval.");
        } catch (Exception e) {
            System.out.println("Collection may already be loaded: " + e.getMessage());
        }
    }

    /**
     * Retrieve top relevant contexts for a query using native Milvus search.
     */
    public List<String> retrieveContext(String query) {
        return retrieveContext(query, 3, 0.6f);
    }

    /**
     * Retrieve top relevant contexts for a query with custom parameters.
     */
    public List<String> retrieveContext(String query, int topK, float minScore) {
        try {
            // Get query embedding
            List<Float> queryEmbedding = embedder.embed(query);

            // Convert to list of lists (required by Milvus search)
            List<List<Float>> vectors = Collections.singletonList(queryEmbedding);

            // Prepare search parameters
            String searchParams = "{\"nprobe\":10}";

            // Perform search - using newer API methods
            R<SearchResults> searchResponse = client.search(SearchParam.newBuilder()
                    .withCollectionName(COLLECTION_NAME)
                    .withMetricType(MetricType.COSINE)
                    .withOutFields(Collections.singletonList(TEXT_FIELD_NAME))
                    .withTopK(topK)
                    .withFloatVectors(vectors)
                    .withVectorFieldName(VECTOR_FIELD_NAME)
                    .withParams(searchParams)
                    .build()
            );

            if (searchResponse.getStatus() != R.Status.Success.getCode()) {
                System.err.println("Search failed: " + searchResponse.getMessage());
                return new ArrayList<>();
            }

            // Process results
            List<String> contexts = new ArrayList<>();
            SearchResults results = searchResponse.getData();

            if (results.getResults().getFieldsDataCount() > 0) {
                // Get text field data and scores
                List<String> textData = results.getResults().getFieldsData(0).getScalars().getStringData().getDataList();

                // Get scores from the results - Milvus returns scores as a repeated field
                int numResults = textData.size();

                for (int i = 0; i < numResults; i++) {
                    // Get the score for this result
                    float score = results.getResults().getScores(i);
                    if (score >= minScore) { // Filter by minimum score
                        String text = textData.get(i);
                        contexts.add(text);
                    }
                }
            }

            System.out.println("Retrieved " + contexts.size() + " relevant results for query: " + query);
            return contexts;

        } catch (Exception e) {
            System.err.println("Error during retrieval: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Close the retriever (connection remains open for reuse)
     */
    public void close() {
        // Note: We don't close the connection here since it's managed by MilvusConnection singleton
        // The connection will be reused by other components
        System.out.println("VectorRetriever closed (connection remains open for reuse).");
    }

    public static void main(String[] args) {
        VectorRetriever retriever = new VectorRetriever();

        try {
            // Example: Query retrieval
            String query = "What is Artificial Intelligence?";
            List<String> contexts = retriever.retrieveContext(query);

            System.out.println("Top retrieved contexts:");
            for (int i = 0; i < contexts.size(); i++) {
                System.out.println((i + 1) + ". " + contexts.get(i));
            }
        } finally {
            retriever.close();
        }
    }
}
