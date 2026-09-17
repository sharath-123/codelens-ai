package com.codelens.codelens_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateReviewRequest(

    @NotBlank(message = "Language is required")
    String language,

    @NotBlank(message = "Source code is required")
    String sourceCode
) {
}
