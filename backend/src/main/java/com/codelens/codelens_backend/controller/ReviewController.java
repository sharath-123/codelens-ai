package com.codelens.codelens_backend.controller;

import com.codelens.codelens_backend.dto.CreateReviewRequest;
import com.codelens.codelens_backend.dto.FindingResponse;
import com.codelens.codelens_backend.dto.ReviewResponse;
import com.codelens.codelens_backend.service.FindingService;
import com.codelens.codelens_backend.service.ReviewService;
import com.codelens.codelens_backend.service.FindingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final FindingService findingService;

    public ReviewController(ReviewService reviewService, FindingService findingService) {
        this.reviewService = reviewService;
        this.findingService = findingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ReviewResponse createReview(
            @Valid @RequestBody CreateReviewRequest request
    ) {

        // Temporary user ID.
        // Identity Platform will replace this later.
        String userId = "demo-user";

        return reviewService.createReview(request, userId);
    }

    @GetMapping("/{id}")
    public ReviewResponse getReview(
            @PathVariable String id
    ) {

        return reviewService.getReview(id);
    }

    @GetMapping("/{id}/findings")
    public List<FindingResponse> getFindings(
            @PathVariable String id
    ) {
        return findingService.getFindings(id);
    }

    @GetMapping
    public List<ReviewResponse> getReviews() {

        // Temporary user ID.
        String userId = "demo-user";

        return reviewService.getUserReviews(userId);
    }
}