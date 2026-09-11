package com.codelens.codelens_backend.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Review {

    private String id;
    private String userId;
    private String language;
    private String sourceCode;
    private ReviewStatus status;
    private Double qualityScore;
    private String Summary;
    private Instant createdAt;
    private Instant completedAt;

}
