package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {
    public List<Film> getFilmsBySearch(String query, String[] by);

    public Film addFilm(Film filmToAdd);

    public Film updFilm(Film filmToUpd);

    public List<Film> getFilms();

    public Optional<Film> getFilmById(int id);

    public void addLike(int filmId, int userLikeId);

    List<Film> getTopMostLikedFilms(int topCount);

    Map<Integer, List<Integer>> getAllLikedFilms();

    List<Film> getRecommendedFilms(List<Integer> recommendedFilmsId);

    public List<Film> getCommonFilms(int userId, int friendId);

    public List<Film> getFilmsByDirectors(int directorId, String sortBy);

    public List<Film> getFilmsBySearch(String fullSort);

    public void delFilmById(int filmId);
}