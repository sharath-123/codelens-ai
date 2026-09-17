package com.codelens.codelens_backend.review;

import com.codelens.codelens_backend.model.Review;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Component;

@Component
public class GeminiReviewEngine {

    private final Client client;

    public GeminiReviewEngine() {
        this.client = Client.builder()
                .vertexAI(true)
                .project(System.getenv("GOOGLE_CLOUD_PROJECT"))
                .location("us-central1")
                .build();
    }

    public String analyze(Review review, String historicalContext) {

        String prompt = buildPrompt(review, historicalContext);

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        prompt,
                        null
        );

        return response.text();
    }

    private String buildPrompt(
            Review review, String historicalContext
    ) {

        return """
                You are CodeLens AI, an expert software code reviewer.

                Analyze the following source code.

                Language:
                %s

                Source Code:
                %s

                Historical Review Guidance:
                %s

                Identify:
                1. Bugs
                2. Security vulnerabilities
                3. Performance problems
                4. Maintainability issues
                5. Architecture issues
                6. Recommended improvements

                Provide a concise professional review.
                """.formatted(
                review.getLanguage(),
                review.getSourceCode(),
                historicalContext
        );
    }
}
