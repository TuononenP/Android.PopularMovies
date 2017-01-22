package com.petrituononen.popularmovies.exceptions;

/**
 * Created by Petri Tuononen on 22.1.2017.
 */
public class NoInternetConnectionException extends Exception {
    public NoInternetConnectionException() {
        super();
    }

    public NoInternetConnectionException(String message) {
        super(message);
    }
}
