package com.codelens.codelens_backend.repository;

import com.codelens.codelens_backend.model.ReviewFinding;

import java.util.List;

public interface FindingRepository
{
    ReviewFinding save(ReviewFinding finding);
    List<ReviewFinding> findByReviewId(String reviewId);
}
