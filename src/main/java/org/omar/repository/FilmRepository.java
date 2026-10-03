package org.omar.repository;


import com.speedment.jpastreamer.application.JPAStreamer;
import com.speedment.jpastreamer.streamconfiguration.StreamConfiguration;
import jakarta.enterprise.context.ApplicationScoped;
import org.omar.model.Film;
import org.omar.model.Film$;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FilmRepository {

    private static final int PAGE_SIZE = 20;

    private final JPAStreamer jpaStreamer;

    public FilmRepository(JPAStreamer jpaStreamer) {
        this.jpaStreamer = jpaStreamer;
    }

    public Optional<Film> findById(short filmId) {
        return jpaStreamer.stream(Film.class)
                .filter(Film$.id.equal(filmId))
                .findFirst();
    }

    public List<Film> findByMinLength(short minLength) {
        return jpaStreamer.stream(Film.class)
                .filter(Film$.length.greaterThan(minLength))
                .sorted(Film$.length)
                .toList();
    }

    public List<Film> findPage(int page, short minLength) {
        return jpaStreamer.stream(Film.class)
                .filter(Film$.length.greaterThan(minLength))
                .sorted(Film$.length)
                .skip((long) page * PAGE_SIZE)
                .limit(PAGE_SIZE)
                .toList();
    }

    public List<Film> findWithActors(String titlePrefix, short minLength) {
        StreamConfiguration<Film> config =
                StreamConfiguration.of(Film.class).joining(Film$.actors);

        return jpaStreamer.stream(config)
                .filter(Film$.title.startsWith(titlePrefix)
                        .and(Film$.length.greaterThan(minLength)))
                .sorted(Film$.length.reversed())
                .toList();
    }
}