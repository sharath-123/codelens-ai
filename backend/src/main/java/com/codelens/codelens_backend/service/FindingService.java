package com.codelens.codelens_backend.service;

import com.codelens.codelens_backend.dto.FindingResponse;
import com.codelens.codelens_backend.repository.FindingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindingService {
    private final FindingRepository findingRepository;
    public FindingService(FindingRepository findingRepository) {
        this.findingRepository = findingRepository;
    }

    public List<FindingResponse> getFindings(String reviewId) {
        return findingRepository.findByReviewId(reviewId)
                .stream()
                .map(finding -> new FindingResponse(
                        finding.getId(),
                        finding.getType(),
                        finding.getSeverity(),
                        finding.getTitle(),
                        finding.getDescription(),
                        finding.getRecommendation(),
                        finding.getLineNumber()
                ))
                .toList();
    }
}
