package com.codelens.codelens_backend.repository;

import com.codelens.codelens_backend.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    Review save(Review review);
    Optional<Review> findById(String id);
    List<Review> findByUserId(String userID);
}
