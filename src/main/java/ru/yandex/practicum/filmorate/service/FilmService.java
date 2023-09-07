package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.constant.EventType;
import ru.yandex.practicum.filmorate.constant.OperationType;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage; //поле куда будет передано хранилище через контструктор с помощью зависимостей
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;
    private final EventStorage eventStorage;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final LocalDate dateForCompare = LocalDate.parse("1895-12-28", formatter);

    //связали зависимостью  сервис и хранилище через аннотацию @RequiredArgsConstructor

    public Film addFilm(Film filmToAdd) {
        releaseDateValid(filmToAdd);
        Film filmAfterAdd = filmStorage.addFilm(filmToAdd);
        LinkedHashSet<Genre> genres = filmAfterAdd.getGenres();
        //инсертим жанры одним батчом
        genreStorage.gernesBatchInsert(genres, filmAfterAdd.getId());

        Set<Director> directors = filmAfterAdd.getDirectors();
        if (directors != null) {
            directorStorage.directorBatchInsert(directors, filmAfterAdd.getId());
        }

        return filmAfterAdd;
    }

    public Film updFilm(Film filmToUpd) {
        releaseDateValid(filmToUpd);
        filmStorage.checkIdInDatabase(filmToUpd.getId());

        Film filmAfterUpd = filmStorage.updFilm(filmToUpd);
        genreStorage.delAllGenresFromFilm(filmAfterUpd.getId());//удаляем жанры чтобы потом записать новые
        LinkedHashSet<Genre> genres = filmAfterUpd.getGenres();
        //инсертим жанры одним батчом
        genreStorage.gernesBatchInsert(genres, filmAfterUpd.getId());

        directorStorage.delAllDirectorsFromFilm(filmAfterUpd.getId());
        Set<Director> directors = filmAfterUpd.getDirectors();
        if (directors != null) {
            directorStorage.directorBatchInsert(directors, filmAfterUpd.getId());
        }

        return filmAfterUpd;
    }

    public List<Film> getFilms() {
        List<Film> tempFilms = filmStorage.getFilms();
        genreStorage.loadGenresForFilm(tempFilms); //обогатили фильмы жанрами
        directorStorage.loadDirectorsForFilm(tempFilms);//обогатили фильмы директорами
        return tempFilms;
    }

    public void delFilmById(int filmId) {
        //реализация фичи в рамках ГП (12 спринт)
        filmStorage.checkIdInDatabase(filmId);

        filmStorage.delFilmById(filmId);
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        userStorage.checkIdInDatabase(userId);
        userStorage.checkIdInDatabase(friendId);

        //реализация фичи в рамках ГП (12 спринт)
        List<Film> tempFilms = filmStorage.getCommonFilms(userId, friendId);
        genreStorage.loadGenresForFilm(tempFilms); //обогатили фильмы жанрами
        return tempFilms;
    }

    public void addLike(int filmId, int userLikeId) {
        userStorage.checkIdInDatabase(userLikeId);
        filmStorage.checkIdInDatabase(filmId);

        filmStorage.addLike(filmId, userLikeId);
        eventStorage.addEvent(userLikeId, filmId, OperationType.ADD, EventType.LIKE);
    }

    public void delLike(int filmId, int userLikeId) {
        userStorage.checkIdInDatabase(userLikeId);
        filmStorage.checkIdInDatabase(filmId);

        filmStorage.delLike(filmId, userLikeId);
        eventStorage.addEvent(userLikeId, filmId, OperationType.REMOVE, EventType.LIKE);
    }

    public Film getFilmById(int id) {
        Film film = filmStorage.getFilmById(id).orElseThrow(() -> new EntityNotFoundException("Фильм не найден в базе"));
        genreStorage.loadGenresForFilm(List.of(film));
        directorStorage.loadDirectorsForFilm(List.of(film));
        return film;
    }

    public List<Film> getTopMostLikedFilms(int topCount, Integer genreId, Integer year) {
        List<Film> popularFilms = filmStorage.getTopMostLikedFilms(topCount, genreId, year);
        genreStorage.loadGenresForFilm(popularFilms);
        directorStorage.loadDirectorsForFilm(popularFilms);

        return popularFilms;
    }

    public List<Film> getFilmsByDirectors(int directorId, FilmSortBy sortBy) {
        List<Film> filmsList;
        if (!directorStorage.contains(directorId)) {
            throw new EntityNotFoundException("отсутствует директора с id - " + directorId);
        }
        if (sortBy.equals(FilmSortBy.year)) {
            filmsList = filmStorage.getFilmsByDirectors(directorId, "release_date");
        } else if (sortBy.equals(FilmSortBy.likes)) {
            filmsList = filmStorage.getFilmsByDirectors(directorId, "total_likes");
        } else {
            throw new ValidationException("запрос на сортировку не верен, sortBy - " + sortBy);
        }
        if (filmsList.isEmpty()) {
            throw new EntityNotFoundException("нет фильмов директора с id - " + directorId);
        }
        genreStorage.loadGenresForFilm(filmsList);
        directorStorage.loadDirectorsForFilm(filmsList);
        return filmsList;
    }

    public List<Film> getFilmsBySearch(String query, String[] by) {
        List<FilmSearchBy> searchBy = Arrays.stream(by).map(FilmSearchBy::valueOf).collect(Collectors.toList());
        List<Film> filmsFound = filmStorage.getFilmsBySearch(query, searchBy);
        genreStorage.loadGenresForFilm(filmsFound);
        directorStorage.loadDirectorsForFilm(filmsFound);
        return filmsFound;
    }

    // методы для валидации
    private void releaseDateValid(Film filmToCheck) {
        LocalDate dateToCheck = filmToCheck.getReleaseDate();
        if (dateToCheck.isBefore(dateForCompare)) {
            log.info("Валидация не пройдена, дата релиза должна быть после 1895-12-28");
            throw new ValidationException("Не пройдена валидация");
        }
    }
}
