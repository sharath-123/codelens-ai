package com.codelens.codelens_backend.repository;

import com.codelens.codelens_backend.model.FindingSeverity;
import com.codelens.codelens_backend.model.FindingType;
import com.codelens.codelens_backend.model.ReviewFinding;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class InMemoryFindingRepository implements FindingRepository {

    private static final String COLLECTION = "findings";

    private final Firestore firestore;

    public InMemoryFindingRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public ReviewFinding save(ReviewFinding finding) {
        try {
            firestore.collection(COLLECTION)
                    .document(finding.getId())
                    .set(toDocumentData(finding))
                    .get();

            return finding;

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while saving finding", exception);

        } catch (ExecutionException exception) {
            throw new RuntimeException("Failed to save finding", exception);
        }
    }

    @Override
    public List<ReviewFinding> findByReviewId(String reviewId) {
        try {
            List<QueryDocumentSnapshot> documents = firestore
                    .collection(COLLECTION)
                    .whereEqualTo("reviewId", reviewId)
                    .get()
                    .get()
                    .getDocuments();

            List<ReviewFinding> findings = new ArrayList<>();

            for (QueryDocumentSnapshot document : documents) {
                findings.add(fromDocument(document));
            }

            return findings;

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while reading findings", exception);

        } catch (ExecutionException exception) {
            throw new RuntimeException("Failed to read findings", exception);
        }
    }

    private java.util.Map<String, Object> toDocumentData(ReviewFinding finding) {
        java.util.Map<String, Object> data = new java.util.HashMap<>();

        data.put("id", finding.getId());
        data.put("reviewId", finding.getReviewId());

        data.put("type", finding.getType() != null
                ? finding.getType().name()
                : null);

        data.put("severity", finding.getSeverity() != null
                ? finding.getSeverity().name()
                : null);

        data.put("title", finding.getTitle());
        data.put("description", finding.getDescription());
        data.put("recommendation", finding.getRecommendation());
        data.put("lineNumber", finding.getLineNumber());

        return data;
    }

    private ReviewFinding fromDocument(DocumentSnapshot document) {
        String typeValue = document.getString("type");
        String severityValue = document.getString("severity");

        FindingType type = typeValue != null
                ? FindingType.valueOf(typeValue)
                : null;

        FindingSeverity severity = severityValue != null
                ? FindingSeverity.valueOf(severityValue)
                : null;

        return ReviewFinding.builder()
                .id(document.getString("id"))
                .reviewId(document.getString("reviewId"))
                .type(type)
                .severity(severity)
                .title(document.getString("title"))
                .description(document.getString("description"))
                .recommendation(document.getString("recommendation"))
                .lineNumber(document.getLong("lineNumber") != null
                        ? document.getLong("lineNumber").intValue()
                        : null)
                .build();
    }
}