package com.codelens.codelens_backend.review;

import com.codelens.codelens_backend.model.ReviewFinding;

import java.util.List;

public record ReviewEngineResult(
        double qualityScore,
        String summary,
        List<ReviewFinding> findings
) {
}