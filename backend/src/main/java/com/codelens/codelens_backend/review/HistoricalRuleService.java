package com.codelens.codelens_backend.review;

import com.codelens.codelens_backend.model.HistoricalRule;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class HistoricalRuleService {

    private final HistoricalRuleLoader historicalRuleLoader;

    public HistoricalRuleService(HistoricalRuleLoader historicalRuleLoader) {
        this.historicalRuleLoader = historicalRuleLoader;
    }

    public List<HistoricalRule> findRelevantRules(
            String type,
            String findingTitle,
            String findingDescription
    ) {

        String searchText = (
                findingTitle + " " + findingDescription
        ).toLowerCase();

        return historicalRuleLoader.getRules()
                .stream()
                .filter(rule -> rule.getType().equalsIgnoreCase(type))
                .sorted(
                        Comparator.comparingInt(
                                (HistoricalRule rule) ->
                                        calculateRelevance(rule, searchText)
                        ).reversed()
                )
                .toList();
    }

    public List<HistoricalRule> findRelevantRules(String type) {
        return historicalRuleLoader.getRules()
                .stream()
                .filter(rule -> rule.getType().equalsIgnoreCase(type))
                .toList();
    }

    public List<HistoricalRule> getAllRules() {
        return historicalRuleLoader.getRules();
    }

    private int calculateRelevance(
            HistoricalRule rule,
            String searchText
    ) {

        String ruleText =
                rule.getDescription().toLowerCase();

        int score = 0;

        String[] keywords = searchText
                .split("[^a-zA-Z0-9]+");

        for (String keyword : keywords) {

            if (keyword.length() < 4) {
                continue;
            }

            if (ruleText.contains(keyword)) {
                score++;
            }
        }

        return score;
    }
}