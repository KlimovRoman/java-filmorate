package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    public void loadGenresForFilm(List<Film> films);

    public void loadGenresForOneFilm(Film film);

    public Optional<Genre> getGenreById(int id);

    public List<Genre> getGenres();
}
