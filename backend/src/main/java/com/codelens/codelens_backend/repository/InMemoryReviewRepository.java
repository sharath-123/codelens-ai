package com.codelens.codelens_backend.repository;

import com.codelens.codelens_backend.model.Review;
import com.codelens.codelens_backend.model.ReviewStatus;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
public class InMemoryReviewRepository implements ReviewRepository {

    private static final String COLLECTION = "reviews";

    private final Firestore firestore;

    public InMemoryReviewRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public Review save(Review review) {
        try {
            firestore.collection(COLLECTION)
                    .document(review.getId())
                    .set(toMap(review))
                    .get();

            return review;

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while saving review", exception);

        } catch (ExecutionException exception) {
            throw new RuntimeException("Failed to save review", exception);
        }
    }

    @Override
    public Optional<Review> findById(String id) {
        try {
            DocumentSnapshot document = firestore.collection(COLLECTION)
                    .document(id)
                    .get()
                    .get();

            if (!document.exists()) {
                return Optional.empty();
            }

            return Optional.of(fromDocument(document));

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while reading review", exception);

        } catch (ExecutionException exception) {
            throw new RuntimeException("Failed to read review", exception);
        }
    }

    @Override
    public List<Review> findByUserId(String userId) {
        try {
            List<QueryDocumentSnapshot> documents = firestore
                    .collection(COLLECTION)
                    .whereEqualTo("userId", userId)
                    .get()
                    .get()
                    .getDocuments();

            List<Review> reviews = new ArrayList<>();

            for (QueryDocumentSnapshot document : documents) {
                reviews.add(fromDocument(document));
            }

            return reviews;

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while reading user reviews", exception);

        } catch (ExecutionException exception) {
            throw new RuntimeException("Failed to read user reviews", exception);
        }
    }

    private Map<String, Object> toMap(Review review) {
        Map<String, Object> data = new HashMap<>();

        data.put("id", review.getId());
        data.put("userId", review.getUserId());
        data.put("language", review.getLanguage());
        data.put("sourceCode", review.getSourceCode());
        data.put("status", review.getStatus() != null
                ? review.getStatus().name()
                : null);
        data.put("qualityScore", review.getQualityScore());
        data.put("summary", review.getSummary());
        data.put("createdAt", review.getCreatedAt() != null
                ? com.google.cloud.Timestamp.ofTimeSecondsAndNanos(
                review.getCreatedAt().getEpochSecond(),
                review.getCreatedAt().getNano())
                : null);
        data.put("completedAt", review.getCompletedAt() != null
                ? com.google.cloud.Timestamp.ofTimeSecondsAndNanos(
                review.getCompletedAt().getEpochSecond(),
                review.getCompletedAt().getNano())
                : null);

        return data;
    }

    private Review fromDocument(DocumentSnapshot document) {
        com.google.cloud.Timestamp createdAtTimestamp =
                document.getTimestamp("createdAt");

        com.google.cloud.Timestamp completedAtTimestamp =
                document.getTimestamp("completedAt");

        Instant createdAt = createdAtTimestamp != null
                ? createdAtTimestamp.toDate().toInstant()
                : null;

        Instant completedAt = completedAtTimestamp != null
                ? completedAtTimestamp.toDate().toInstant()
                : null;

        String statusValue = document.getString("status");

        ReviewStatus status = statusValue != null
                ? ReviewStatus.valueOf(statusValue)
                : null;

        return Review.builder()
                .id(document.getString("id"))
                .userId(document.getString("userId"))
                .language(document.getString("language"))
                .sourceCode(document.getString("sourceCode"))
                .status(status)
                .qualityScore(document.getDouble("qualityScore"))
                .summary(document.getString("summary"))
                .createdAt(createdAt)
                .completedAt(completedAt)
                .build();
    }
}