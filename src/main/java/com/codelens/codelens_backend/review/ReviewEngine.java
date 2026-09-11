package com.codelens.codelens_backend.review;

import com.codelens.codelens_backend.model.Review;

public interface ReviewEngine {

    ReviewEngineResult analyze(Review review);
}