package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class FilmControllerTest {

    @Test
    public void dateValidMinTest(){
        Film film = new Film();
        film.setName("name");
        film.setDuration(10.00);
        film.setDescription("des");
        film.setReleaseDate("1895-12-27");
        FilmController controller = new FilmController();
        final RuntimeException excep = assertThrows(RuntimeException.class, () -> controller.addFilm(film));
        assertEquals("Не пройдена валидация", excep.getMessage());
    }

    @Test
    public void dateValidEqualTest(){
        Film film = new Film();
        film.setName("name");
        film.setDuration(10.00);
        film.setDescription("des");
        film.setReleaseDate("1895-12-28");
        FilmController controller = new FilmController();
        final RuntimeException excep = assertThrows(RuntimeException.class, () -> controller.addFilm(film));
        assertEquals("Не пройдена валидация", excep.getMessage());
    }

    @Test
    public void dateValidMaxTest(){
        Film film = new Film();
        film.setName("name");
        film.setDuration(10.00);
        film.setDescription("des");
        film.setReleaseDate("2005-12-28");
        FilmController controller = new FilmController();
        controller.addFilm(film);
        List<Film> list = controller.getFilms();
        assertEquals(1, list.size());

    }
}
