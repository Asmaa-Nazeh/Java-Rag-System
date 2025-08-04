package LLM;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.model.ollama.OllamaLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.memory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.retriever.Retriever;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.util.List;

@Service
public class RagPipeline {

    private ConversationalRetrievalChain chain;
    private EmbeddingStore<TextSegment> embeddingStore;

    @PostConstruct
    public void init() {
        // 1. Initialize LLM (Ollama)
        var llm = OllamaLanguageModel.builder()
                .baseUrl("http://localhost:11434") // Ollama default
                .modelName("mistral")              // Change model if needed
                .temperature(0.2)
                .build();

        // 2. Initialize embedding model
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        // 3. In-memory vector store (for demo)
        embeddingStore = new InMemoryEmbeddingStore<>();

        // 4. Create retriever
        Retriever retriever = new EmbeddingStoreRetriever<>(embeddingStore, embeddingModel);

        // 5. Build RAG chain
        chain = ConversationalRetrievalChain.builder()
                .llm(llm)
                .retriever(retriever)
                .build();

        // 6. Insert some sample documents (simulate ingestion)
        List<Document> docs = List.of(
                Document.from("LangChain4j is a Java library for building LLM applications."),
                Document.from("RAG combines retrieval and generation for better answers."),
                Document.from("Ollama lets you run LLMs locally on your machine.")
        );

        for (Document doc : docs) {
            var embedding = embeddingModel.embed(doc.text());
            embeddingStore.add(embedding, TextSegment.from(doc.text()));
        }
    }

    public String answerQuestion(String question) {
        return chain.execute(question).content();
    }
}