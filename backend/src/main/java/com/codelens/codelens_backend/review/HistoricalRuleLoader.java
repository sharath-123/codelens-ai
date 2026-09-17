package com.codelens.codelens_backend.review;

import com.codelens.codelens_backend.model.HistoricalRule;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class HistoricalRuleLoader {

    private final List<HistoricalRule> rules = new ArrayList<>();

    public HistoricalRuleLoader() {
        loadRules();
    }

    private void loadRules() {
        try {
            ClassPathResource resource =
                    new ClassPathResource("historical-rules.csv");

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            resource.getInputStream(),
                            StandardCharsets.UTF_8))) {

                String line;

                // Skip CSV header
                reader.readLine();

                while ((line = reader.readLine()) != null) {

                    if (line.isBlank()) {
                        continue;
                    }

                    String[] columns = line.split(",", 3);

                    if (columns.length < 3) {
                        continue;
                    }

                    HistoricalRule rule = HistoricalRule.builder()
                            .id(columns[0].trim())
                            .type(columns[1].trim())
                            .description(columns[2].trim())
                            .build();

                    rules.add(rule);
                }
            }

            System.out.println(
                    "Loaded " + rules.size() + " historical review rules."
            );

        } catch (Exception exception) {
            throw new RuntimeException(
                    "Failed to load historical review rules",
                    exception
            );
        }
    }

    public List<HistoricalRule> getRules() {
        return List.copyOf(rules);
    }
}
