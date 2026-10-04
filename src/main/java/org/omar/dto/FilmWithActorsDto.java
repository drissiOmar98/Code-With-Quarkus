package org.omar.dto;


import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.omar.model.Film;

import java.util.List;

@Schema(description = "A film together with its cast")
public record FilmWithActorsDto(Short id, String title, Short length, List<ActorDto> actors) {

    public static FilmWithActorsDto from(Film film) {
        return new FilmWithActorsDto(
                film.getFilmId(),
                film.getTitle(),
                film.getLength(),
                film.getActors().stream().map(ActorDto::from).toList());
    }
}