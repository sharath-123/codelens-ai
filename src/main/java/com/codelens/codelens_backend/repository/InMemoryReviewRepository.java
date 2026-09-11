package com.codelens.codelens_backend.repository;

import com.codelens.codelens_backend.model.Review;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryReviewRepository implements ReviewRepository {

    private final ConcurrentHashMap<String, Review> reviews =
            new ConcurrentHashMap<>();

    @Override
    public Review save(Review review) {
        reviews.put(review.getId(), review);
        return review;
    }

    @Override
    public Optional<Review> findById(String id) {
        return Optional.ofNullable(reviews.get(id));
    }

    @Override
    public List<Review> findByUserId(String userId) {

        return reviews.values()
                .stream()
                .filter(review -> review.getUserId().equals(userId))
                .toList();
    }
}