package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    public Film addFilm(Film filmToAdd);
    public Film updFilm(Film filmToUpd);
    public List<Film> getFilms();
    public Film getFilmById(int id);
}
