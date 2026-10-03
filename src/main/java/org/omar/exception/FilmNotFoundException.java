package org.omar.exception;

public class FilmNotFoundException extends RuntimeException {

    public FilmNotFoundException(short filmId) {
        super("No film found with id " + filmId);
    }
}