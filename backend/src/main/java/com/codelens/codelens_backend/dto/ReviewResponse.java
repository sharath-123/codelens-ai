package com.codelens.codelens_backend.dto;

import com.codelens.codelens_backend.model.ReviewStatus;

import java.time.Instant;


public record ReviewResponse (

    String id,
    String userId,
    String language,
    ReviewStatus status,
    Double qualityScore,
    String summary,
    Instant createdAt,
    Instant completedAt
    ) {
}
