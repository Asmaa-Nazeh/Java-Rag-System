package LLM;

import dev.langchain4j.model.ollama.OllamaLanguageModel;
import org.springframework.stereotype.Component;

@Component
public class LLMClient {

    private final OllamaLanguageModel llm;

    public LLMClient() {
        llm = OllamaLanguageModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("gemma3:1b")
                .temperature(0.2)
                .build();
    }

    public String generateAnswer(String prompt) {
        return llm.generate(prompt).content();
    }
}
