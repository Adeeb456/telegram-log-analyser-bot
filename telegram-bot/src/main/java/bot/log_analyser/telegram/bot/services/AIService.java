package bot.log_analyser.telegram.bot.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AIService {

    private String apiKey = "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";

    private final String URL = "https://openrouter.ai/api/v1/chat/completions";

    public String analyseLogs(String logs) {

        WebClient webClient = WebClient.builder()
                .baseUrl("https://openrouter.ai/api/v1")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey.trim())
                .defaultHeader("HTTP-Referer", "http://localhost:8080")
                .defaultHeader("X-Title", "AI Debug Bot")
                .build();

//        String jsonBody = """
//            {
//              "model":"openrouter/free",
//              "messages": [
//                {
//                  "role": "user",
//                  "content": "%s"
//                }
//              ]
//            }
//            """.formatted(logs);

        String jsonBody = """
        {
          "model": "openrouter/elephant-alpha",
          "messages": [
            {
              "role": "system",
              "content": "You are a technical log analysis assistant. Your ONLY job is to analyze technical logs, errors, stack traces, and IT/software related issues. Follow these strict rules:\\n\\n1. If the user's message is NOT related to tech, programming, errors, logs, AI, or IT — respond ONLY with this exact JSON: {\\"type\\": \\"non_technical\\", \\"message\\": \\"I am here to solve your technical queries only. Please paste your logs or describe your technical issue.\\"}\\n\\n2. If the message IS technical, respond ONLY with this exact JSON format, no extra text:\\n{\\n  \\"type\\": \\"technical\\",\\n  \\"title\\": \\"<short title of the issue, max 10 words>\\",\\n  \\"root_cause\\": \\"<what caused the error, max 50 words>\\",\\n  \\"fix\\": \\"<step by step fix, max 100 words>\\",\\n  \\"severity\\": \\"<LOW | MEDIUM | HIGH | CRITICAL>\\"\\n}\\n\\nNEVER respond with plain text. ALWAYS respond with valid JSON only."
            },
            {
              "role": "user",
              "content": \""""  + logs + """
              "
            }
          ]
        }
        """;

        try {
            String response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(jsonBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .doOnNext(errorBody -> System.out.println("Error Body: " + errorBody))
                                    .flatMap(errorBody -> Mono.error(new RuntimeException("API Error: " + errorBody)))
                    )
                    .bodyToMono(String.class)
                    .block();

            // Extract content from API response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            String aiContent = root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            return aiContent;

        } catch (Exception e) {
            e.printStackTrace();
            return "Server is handling many requests at the moment. Please try again after some time.";
        }
    }

}
