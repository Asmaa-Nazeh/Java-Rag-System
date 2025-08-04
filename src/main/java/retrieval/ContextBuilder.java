package retrieval;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ContextBuilder {
    public String buildContext(List<String> retrievedChunks) {
        return String.join("\n", retrievedChunks);
    }
}