package repository;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.omar.model.Film;
import org.omar.repository.FilmRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class FilmRepositoryTest {

    @Inject
    FilmRepository filmRepository;

    // ---------- findById ----------

    @Test
    void findById_existingFilm_returnsFilm() {
        Optional<Film> film = filmRepository.findById((short) 1);

        assertTrue(film.isPresent());
        assertEquals("ACADEMY DINOSAUR", film.get().getTitle());
        assertEquals((short) 86, film.get().getLength());
    }

    @Test
    void findById_unknownFilm_returnsEmpty() {
        assertTrue(filmRepository.findById((short) 9999).isEmpty());
    }

    // ---------- findByMinLength ----------

    @Test
    void findByMinLength_returnsOnlyLongerFilmsSortedByLength() {
        List<Film> films = filmRepository.findByMinLength((short) 150);

        assertFalse(films.isEmpty());
        assertTrue(films.stream().allMatch(f -> f.getLength() > 150));
        assertSortedByLengthAscending(films);
    }

    @Test
    void findByMinLength_aboveLongestFilm_returnsEmptyList() {
        assertTrue(filmRepository.findByMinLength((short) 200).isEmpty());
    }

    // ---------- findPage ----------

    @Test
    void findPage_firstPage_returnsTwentyFilmsSortedByLength() {
        List<Film> page = filmRepository.findPage(0, (short) 0);

        assertEquals(20, page.size());
        assertSortedByLengthAscending(page);
    }

    @Test
    void findPage_lastPage_isFullAndNextPageIsEmpty() {
        // Sakila contains 1000 films: pages 0..49 are full, page 50 is empty
        assertEquals(20, filmRepository.findPage(49, (short) 0).size());
        assertTrue(filmRepository.findPage(50, (short) 0).isEmpty());
    }

    @Test
    void findPage_appliesMinLengthFilter() {
        List<Film> page = filmRepository.findPage(0, (short) 120);

        assertFalse(page.isEmpty());
        assertTrue(page.stream().allMatch(f -> f.getLength() > 120));
    }

    // ---------- findWithActors ----------

    @Test
    void findWithActors_returnsFilmsWithTheirCast() {
        List<Film> films = filmRepository.findWithActors("ACADEMY", (short) 0);

        assertFalse(films.isEmpty());
        Film film = films.get(0);
        assertEquals("ACADEMY DINOSAUR", film.getTitle());
        assertFalse(film.getActors().isEmpty());
        assertTrue(film.getActors().stream().allMatch(a -> a.getFirstName() != null));
    }

    @Test
    void findWithActors_sortsByLengthDescending() {
        List<Film> films = filmRepository.findWithActors("A", (short) 100);

        assertFalse(films.isEmpty());
        for (int i = 1; i < films.size(); i++) {
            assertTrue(films.get(i - 1).getLength() >= films.get(i).getLength());
        }
    }

    @Test
    void findWithActors_noMatchingTitle_returnsEmptyList() {
        assertTrue(filmRepository.findWithActors("ZZZ-NO-SUCH-TITLE", (short) 0).isEmpty());
    }

    // ---------- helpers ----------

    private static void assertSortedByLengthAscending(List<Film> films) {
        for (int i = 1; i < films.size(); i++) {
            assertTrue(films.get(i - 1).getLength() <= films.get(i).getLength(),
                    "Films are not sorted by length at index " + i);
        }
    }
}