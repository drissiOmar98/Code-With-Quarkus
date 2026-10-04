package org.omar.dto;


import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.omar.model.Film;

import java.math.BigDecimal;

@Schema(description = "A film from the Sakila catalog")
public record FilmDto(
        @Schema(example = "1") Short id,
        @Schema(example = "ACADEMY DINOSAUR") String title,
        @Schema(description = "Duration in minutes", example = "86") Short length,
        @Schema(example = "0.99") BigDecimal rentalRate) {

    public static FilmDto from(Film film) {
        return new FilmDto(film.getFilmId(), film.getTitle(), film.getLength(), film.getRentalRate());
    }
}