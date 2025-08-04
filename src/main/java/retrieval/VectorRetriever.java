package retrieval;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VectorRetriever {

    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStoreRetriever<TextSegment> retriever;

    public VectorRetriever() {

        // Embedding Model (384 dim)
        embeddingModel = new dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel();

        // Milvus Store
        embeddingStore = MilvusEmbeddingStore.builder()
                .host("localhost")
                .port(19530)
                .collectionName("rag_collection")
                .dimension(384)
                .build();

        // Retriever
        retriever = new EmbeddingStoreRetriever<>(embeddingStore, embeddingModel, 3);
    }

    public List<String> retrieveContext(String query) {
        return retriever.retrieve(query)
                .stream()
                .map(r -> r.content().text())
                .collect(Collectors.toList());
    }

    public EmbeddingStore<TextSegment> getEmbeddingStore() {
        return embeddingStore;
    }

    public EmbeddingModel getEmbeddingModel() {
        return embeddingModel;
    }
}