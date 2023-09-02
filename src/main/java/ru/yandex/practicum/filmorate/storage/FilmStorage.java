package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {
    Film addFilm(Film filmToAdd);

    Film updFilm(Film filmToUpd);

    List<Film> getFilms();

    Optional<Film> getFilmById(int id);

    void addLike(int filmId, int userLikeId);

    void delLike(int filmId, int userLikeId);

    List<Film> getTopMostLikedFilms(int topCount);

    Optional<Map<Integer, List<Integer>>> getAllLikedFilms();

    Optional<List<Film>> getRecommendedFilms(String rangeId);
}