package org.omar.service.Impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.omar.dto.FilmDto;
import org.omar.dto.FilmWithActorsDto;
import org.omar.exception.FilmNotFoundException;
import org.omar.model.Film;
import org.omar.repository.FilmRepository;
import org.omar.service.FilmService;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class FilmServiceImpl implements FilmService {

    private final FilmRepository filmRepository;

    public FilmServiceImpl(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    @Override
    public FilmDto getFilm(short filmId) {
        return filmRepository.findById(filmId)
                .map(FilmDto::from)
                .orElseThrow(() -> new FilmNotFoundException(filmId));
    }

    @Override
    public List<FilmDto> getFilms(int page, short minLength) {
        return filmRepository.findPage(page, minLength).stream()
                .map(FilmDto::from)
                .toList();
    }

    @Override
    public List<FilmWithActorsDto> getFilmsWithActors(String titlePrefix, short minLength) {
        return filmRepository.findWithActors(titlePrefix, minLength).stream()
                .map(FilmWithActorsDto::from)
                .toList();
    }

    @Override
    @Transactional
    public List<FilmDto> updateRentalRate(short minLength, BigDecimal rentalRate) {
        List<Film> films = filmRepository.findByMinLength(minLength);
        // Entities are managed inside the transaction: changes are flushed on commit
        films.forEach(film -> film.setRentalRate(rentalRate));
        return films.stream().map(FilmDto::from).toList();
    }
}