package com.codelens.codelens_backend.service;

import com.codelens.codelens_backend.dto.CreateReviewRequest;
import com.codelens.codelens_backend.dto.ReviewResponse;
import com.codelens.codelens_backend.exception.ReviewNotFoundException;
import com.codelens.codelens_backend.model.Review;
import com.codelens.codelens_backend.model.ReviewStatus;
import com.codelens.codelens_backend.repository.FindingRepository;
import com.codelens.codelens_backend.repository.ReviewRepository;
import com.codelens.codelens_backend.review.GeminiReviewEngine;
import com.codelens.codelens_backend.review.ReviewEngine;
import com.codelens.codelens_backend.review.ReviewEngineResult;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final FindingRepository findingRepository;
    private final ReviewEngine reviewEngine;
    private final GeminiReviewEngine geminiReviewEngine;

    public ReviewService(
            ReviewRepository reviewRepository,
            FindingRepository findingRepository,
            ReviewEngine reviewEngine,
            GeminiReviewEngine geminiReviewEngine
    ) {
        this.reviewRepository = reviewRepository;
        this.findingRepository = findingRepository;
        this.reviewEngine = reviewEngine;
        this.geminiReviewEngine = geminiReviewEngine;
    }

    public ReviewResponse createReview(
            CreateReviewRequest request,
            String userId
    ) {

        // Create a new review
        Review review = Review.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .language(request.language())
                .sourceCode(request.sourceCode())
                .status(ReviewStatus.QUEUED)
                .createdAt(Instant.now())
                .build();

        // Save initial review
        reviewRepository.save(review);

        try {

            // Run deterministic local analysis
            ReviewEngineResult localResult =
                    reviewEngine.analyze(review);

            // Build historical context from local findings
            String historicalContext =
                    localResult.findings()
                            .stream()
                            .map(finding ->
                                    finding.getType()
                                            + ": "
                                            + finding.getTitle()
                                            + " - "
                                            + finding.getDescription()
                            )
                            .reduce(
                                    "",
                                    (current, finding) ->
                                            current + finding + "\n"
                            );

            // Send the code and findings to Gemini
            String geminiReview =
                    geminiReviewEngine.analyze(
                            review,
                            historicalContext
                    );

            // Keep deterministic score from local analyzer
            review.setQualityScore(
                    localResult.qualityScore()
            );

            // Store Gemini's intelligent review
            review.setSummary(geminiReview);

            review.setStatus(ReviewStatus.COMPLETED);
            review.setCompletedAt(Instant.now());

            // Save final review
            reviewRepository.save(review);

            // Save deterministic findings
            localResult.findings()
                    .forEach(findingRepository::save);

        } catch (Exception exception) {

            review.setStatus(ReviewStatus.FAILED);
            review.setSummary(
                    "Review processing failed: "
                            + exception.getMessage()
            );
            review.setCompletedAt(Instant.now());

            reviewRepository.save(review);

            throw exception;
        }

        return toResponse(review);
    }

    public ReviewResponse getReview(String id) {

        Review review = reviewRepository.findById(id)
                .orElseThrow(() ->
                        new ReviewNotFoundException(
                                "Review not found: " + id
                        ));

        return toResponse(review);
    }

    public List<ReviewResponse> getUserReviews(String userId) {

        return reviewRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ReviewResponse toResponse(Review review) {

        return new ReviewResponse(
                review.getId(),
                review.getUserId(),
                review.getLanguage(),
                review.getStatus(),
                review.getQualityScore(),
                review.getSummary(),
                review.getCreatedAt(),
                review.getCompletedAt()
        );
    }
}