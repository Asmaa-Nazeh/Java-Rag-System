package LLM;

import java.util.Scanner;

public class AppRag {

    private final RagPipeline ragPipeline;

    public AppRag() {
        // Initialize components
        System.out.println("Initializing RAG System...");

        // Initialize RagPipeline - constructor handles all initialization
        this.ragPipeline = new RagPipeline();

        System.out.println("RAG System initialized successfully!");
    }

    public String answerQuestion(String question) {
        System.out.println("Processing question with RAG Pipeline...");

        try {
            String answer = ragPipeline.answerQuestion(question);
            System.out.println("Answer generated successfully!");
            return answer;
        } catch (Exception e) {
            System.err.println("Error in RAG Pipeline: " + e.getMessage());
            return "Sorry, I encountered an error while processing your question. Please try again.";
        }
    }

    public void runInteractiveSession() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Welcome to the Java RAG System!");
        System.out.println("Ask me questions based on your documents.");
        System.out.println("Type 'exit', 'quit', or 'q' to end the session.");
        System.out.println("=".repeat(60) + "\n");

        while (true) {
            System.out.print("Your question: ");
            String userInput = scanner.nextLine().trim();

            // Check for exit commands
            if (userInput.equalsIgnoreCase("exit") ||
                userInput.equalsIgnoreCase("quit") ||
                userInput.equalsIgnoreCase("q")) {
                System.out.println("\nThank you for using the RAG System. Goodbye!");
                break;
            }

            // Skip empty input
            if (userInput.isEmpty()) {
                System.out.println("Please enter a question.");
                continue;
            }

            try {
                // Process the question
                System.out.println("\n" + "-".repeat(40));
                String answer = answerQuestion(userInput);
                System.out.println("\nAnswer:");
                System.out.println(answer);
                System.out.println("-".repeat(40) + "\n");

            } catch (Exception e) {
                System.err.println("Error processing your question: " + e.getMessage());
                System.out.println("Please try again with a different question.\n");
            }
        }

        // Clean up resources
        scanner.close();
    }


    public static void main(String[] args) {
        try {
            AppRag ragApp = new AppRag();
            ragApp.runInteractiveSession();

        } catch (Exception e) {
            System.err.println(" Failed to start RAG application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
