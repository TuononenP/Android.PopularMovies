package com.petrituononen.popularmovies.exceptions;

/**
 * Created by petri on 18.2.2017.
 */

public class ApiKeyNotFoundException extends Exception {
    public ApiKeyNotFoundException() {
        super();
    }

    public ApiKeyNotFoundException(String message) {
        super(message);
    }
}
