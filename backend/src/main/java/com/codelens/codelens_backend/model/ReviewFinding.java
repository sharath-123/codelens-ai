package com.codelens.codelens_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewFinding {
    private String id;
    private String reviewId;

    private FindingType type;
    private FindingSeverity severity;

    private String title;
    private String description;
    private String recommendation;

    private Integer lineNumber;
}
