package com.codelens.codelens_backend.review;

import com.codelens.codelens_backend.model.HistoricalRule;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoricalRuleService {

    private final HistoricalRuleLoader historicalRuleLoader;

    public HistoricalRuleService(HistoricalRuleLoader historicalRuleLoader) {
        this.historicalRuleLoader = historicalRuleLoader;
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
}