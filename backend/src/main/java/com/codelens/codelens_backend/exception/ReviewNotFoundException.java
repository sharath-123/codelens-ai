package com.codelens.codelens_backend.exception;

public class ReviewNotFoundException extends RuntimeException{

    public ReviewNotFoundException(String message) {
        super(message);
    }
}
