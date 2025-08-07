package LLM;

import dev.langchain4j.model.ollama.OllamaLanguageModel;
import retrieval.VectorRetriever;

import java.util.List;

public class RagPipeline {

    private final VectorRetriever vectorRetriever;
    private final OllamaLanguageModel llm;

    // Default constructor
    public RagPipeline() {
        this.vectorRetriever = new VectorRetriever();
        this.llm = createLlm();
    }

    private OllamaLanguageModel createLlm() {
        return OllamaLanguageModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("gemma3:1b")
                .temperature(0.2)
                .build();
    }

    public String answerQuestion(String question) {
        // Retrieve relevant context from Milvus
        List<String> contexts = vectorRetriever.retrieveContext(question);

        // Combine contexts into a single string
        String combinedContext = String.join("\n", contexts);

        // Create prompt with retrieved context
        String prompt = String.format(
                "Context: %s\n\nQuestion: %s\n\nAnswer based on the context provided:",
                combinedContext, question);

        // Generate answer using LLM
        return llm.generate(prompt).content();
    }
}