package com.codelens.codelens_backend.repository;

import com.codelens.codelens_backend.model.ReviewFinding;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryFindingRepository implements FindingRepository {

    private final ConcurrentHashMap<String, ReviewFinding> findings =
            new ConcurrentHashMap<>();

    @Override
    public ReviewFinding save(ReviewFinding finding) {
        findings.put(finding.getId(), finding);
        return finding;
    }

    @Override
    public List<ReviewFinding> findByReviewId(String reviewId) {
        return findings.values()
                .stream()
                .filter(finding ->
                        finding.getReviewId().equals(reviewId))
                .toList();
    }
}