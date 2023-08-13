package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class FilmControllerTest {

    @Test
    public void dateValidMinTest() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date =  LocalDate.parse("1895-12-27",formatter);
        Film film = new Film();
        film.setName("name");
        film.setDuration(10.00);
        film.setDescription("des");
        film.setReleaseDate(date);
        FilmStorage store = new InMemoryFilmStorage();
        final RuntimeException excep = assertThrows(RuntimeException.class, () -> store.addFilm(film));
        assertEquals("Не пройдена валидация", excep.getMessage());
    }

    @Test
    public void dateValidEqualTest() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date =  LocalDate.parse("1895-12-28",formatter);
        Film film = new Film();
        film.setName("name");
        film.setDuration(10.00);
        film.setDescription("des");
        film.setReleaseDate(date);
        FilmStorage store = new InMemoryFilmStorage();
        final RuntimeException excep = assertThrows(RuntimeException.class, () -> store.addFilm(film));
        assertEquals("Не пройдена валидация", excep.getMessage());
    }

    @Test
    public void dateValidMaxTest() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date =  LocalDate.parse("2005-12-28",formatter);
        Film film = new Film();
        film.setName("name");
        film.setDuration(10.00);
        film.setDescription("des");
        film.setReleaseDate(date);
        FilmStorage store = new InMemoryFilmStorage();
        store.addFilm(film);
        List<Film> list = store.getFilms();
        assertEquals(1, list.size());
    }
}