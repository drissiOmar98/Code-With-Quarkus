package org.omar.service;



import org.omar.dto.FilmDto;
import org.omar.dto.FilmWithActorsDto;

import java.math.BigDecimal;
import java.util.List;

public interface FilmService {


    FilmDto getFilm(short filmId);

    List<FilmDto> getFilms(int page, short minLength);

    List<FilmWithActorsDto> getFilmsWithActors(String titlePrefix, short minLength);

    /** Sets the rental rate of every film longer than minLength and returns the updated films. */
    List<FilmDto> updateRentalRate(short minLength, BigDecimal rentalRate);
}