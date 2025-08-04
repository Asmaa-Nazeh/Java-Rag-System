package api;

import api.dto.QuestionRequest;
import LLM.RagPipeline;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagPipeline ragPipeline;

    public RagController(RagPipeline ragPipeline) {
        this.ragPipeline = ragPipeline;
    }

    @PostMapping("/ask")
    public String askQuestion(@RequestBody QuestionRequest request) {
        return ragPipeline.answerQuestion(request.getQuestion());
    }
}