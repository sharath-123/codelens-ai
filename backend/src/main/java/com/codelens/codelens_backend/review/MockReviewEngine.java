package com.codelens.codelens_backend.review;

import com.codelens.codelens_backend.model.FindingSeverity;
//import com.codelens.codelens_backend.model.FindingType;
import com.codelens.codelens_backend.model.Review;
import com.codelens.codelens_backend.model.ReviewFinding;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MockReviewEngine implements ReviewEngine {

    private final LocalCodeAnalyzer localCodeAnalyzer;

    public MockReviewEngine(LocalCodeAnalyzer localCodeAnalyzer) {
        this.localCodeAnalyzer = localCodeAnalyzer;
    }

    @Override
    public ReviewEngineResult analyze(Review review) {

        List<ReviewFinding> findings =
                localCodeAnalyzer.analyze(review);

        double score = calculateScore(findings);

        String summary;

        if (findings.isEmpty()) {
            summary = "No issues were detected by the local analyzer.";
        } else {
            summary = "The code contains "
                    + findings.size()
                    + " potential issue(s) that should be reviewed.";
        }

        return new ReviewEngineResult(
                score,
                summary,
                findings
        );
    }

    private double calculateScore(List<ReviewFinding> findings) {

        double score = 10.0;

        for (ReviewFinding finding : findings) {

            if (finding.getSeverity() == FindingSeverity.CRITICAL) {
                score -= 3.0;
            } else if (finding.getSeverity() == FindingSeverity.HIGH) {
                score -= 2.0;
            } else if (finding.getSeverity() == FindingSeverity.MEDIUM) {
                score -= 1.0;
            } else if (finding.getSeverity() == FindingSeverity.LOW) {
                score -= 0.25;
            }
        }

        return Math.max(1.0, Math.round(score * 10.0) / 10.0);
    }
}