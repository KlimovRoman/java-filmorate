package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    public List<Film> getFilmsByDirectors(int directorId, String sortBy);

    public List<Film> getFilmsBySearch(String query, String[] by);

    public Film addFilm(Film filmToAdd);

    public Film updFilm(Film filmToUpd);

    public List<Film> getFilms();

    public Optional<Film> getFilmById(int id);

    public void addLike(int filmId, int userLikeId);

    public void delLike(int filmId, int userLikeId);

    public List<Film> getTopMostLikedFilms(int topCount);

    public List<Film> getCommonFilms(int userId, int friendId);

    public void delFilmById(int filmId);

    List<Integer> getRecommendedFilmsID(Integer userId);

    List<Film> getRecommendedFilms(List<Integer> recommendedFilmsId);
}