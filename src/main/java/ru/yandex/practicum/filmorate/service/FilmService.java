package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage; //поле куда будет передано хранилище через контструктор с помощью зависимостей
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final LocalDate dateForCompare =  LocalDate.parse("1895-12-28",formatter);

    //связали зависимостью  сервис и хранилище
    @Autowired
    public FilmService(FilmStorage filmStorage, GenreStorage genreStorage, DirectorStorage directorStorage) {
        this.filmStorage = filmStorage;
        this.genreStorage = genreStorage;
        this.directorStorage = directorStorage;
    }

    public Film addFilm(Film filmToAdd) {
        releaseDateValid(filmToAdd);
        Film filmAfterAdd = filmStorage.addFilm(filmToAdd);
        LinkedHashSet<Genre> genres =  filmAfterAdd.getGenres();
        //инсертим жанры одним батчом
        genreStorage.gernesBatchInsert(genres, filmAfterAdd.getId());

        Set<Director> directors = filmAfterAdd.getDirectors();
        directorStorage.directorBatchInsert(directors, filmAfterAdd.getId());

        return filmAfterAdd;
    }

    public Film updFilm(Film filmToUpd) {
        releaseDateValid(filmToUpd);
        Film filmAfterUpd = filmStorage.updFilm(filmToUpd);
        genreStorage.delAllGenresFromFilm(filmAfterUpd.getId());//удаляем жанры чтобы потом записать новые
        LinkedHashSet<Genre> genres =  filmAfterUpd.getGenres();
        //инсертим жанры одним батчом
        genreStorage.gernesBatchInsert(genres, filmAfterUpd.getId());

        directorStorage.delAllDirectorsFromFilm(filmAfterUpd.getId());
        Set<Director> directors = filmAfterUpd.getDirectors();
        directorStorage.directorBatchInsert(directors, filmAfterUpd.getId());

        return filmAfterUpd;
    }

    public List<Film> getFilms() {
        List<Film> tempFilms =  filmStorage.getFilms();
        genreStorage.loadGenresForFilm(tempFilms); //обогатили фильмы жанрами
        directorStorage.loadDirectorsForFilm(tempFilms);//обогатили фильмы директорами
        return tempFilms;
    }

    public void delFilmById(int filmId) {
        //реализация фичи в рамках ГП (12 спринт)
        filmStorage.delFilmById(filmId);
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        //реализация фичи в рамках ГП (12 спринт)
        List<Film> tempFilms =  filmStorage.getCommonFilms(userId, friendId);
        genreStorage.loadGenresForFilm(tempFilms); //обогатили фильмы жанрами
        return tempFilms;
    }

    public void addLike(int filmId, int userLikeId) {
        filmStorage.addLike(filmId, userLikeId);
    }

    public void delLike(int filmId, int userLikeId) {
        filmStorage.delLike(filmId, userLikeId);
    }

    public Film getFilmById(int id) {
       Film film =  filmStorage.getFilmById(id).orElseThrow(() -> new EntityNotFoundException("Фильм не найден в базе"));
       genreStorage.loadGenresForFilm(List.of(film));
       directorStorage.loadDirectorsForFilm(List.of(film));
       return film;
    }

    public List<Film> getTopMostLikedFilms(int topCount) {
        List<Film> listForGenresUpd = filmStorage.getTopMostLikedFilms(topCount);
        genreStorage.loadGenresForFilm(listForGenresUpd);
        directorStorage.loadDirectorsForFilm(listForGenresUpd);
        return listForGenresUpd;
    }

    // методы для валидации
    private void releaseDateValid(Film filmToCheck) {
        LocalDate dateToCheck = filmToCheck.getReleaseDate();
        if (dateToCheck.isBefore(dateForCompare)) {
            log.info("Валидация не пройдена, дата релиза должна быть после 1895-12-28");
            throw new ValidationException("Не пройдена валидация");
        }
    }

    public List<Film> getFilmsByDirectors(int directorId, String sortBy) {//to do
        List<Film> filmsList;
        if (!directorStorage.contains(directorId)) {
            throw new EntityNotFoundException("отсутствует директора с id - " + directorId);
        }
        if (sortBy.equals("year")) {
            filmsList = filmStorage.getFilmsByDirectors(directorId, "release_date");
        } else if (sortBy.equals("likes")) {
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
        String fullSort = "";
        for (String sortPart : by) {
            if (!fullSort.isBlank()) {
                fullSort = fullSort + " or ";
            }
            if (sortPart.equals("director")) {
                //fullSort = fullSort + "d.name_director like '%" + query + "%'";
                fullSort = fullSort + " f.id in (select df.film_id from director_films as df join director as d" +
                        " on df.director_id = d.id where lower(d.name_director) like '%" + query.toLowerCase() + "%') ";
            } else if (sortPart.equals("title")) {
                fullSort = fullSort + " lower(f.name) like '%" + query.toLowerCase() + "%' ";
            } else {
                throw new ValidationException("часть запроса by ошибочна - " + sortPart);
            }
        }
        System.out.println(fullSort);
        List<Film> filmsFound = filmStorage.getFilmsBySearch(fullSort);
        genreStorage.loadGenresForFilm(filmsFound);
        directorStorage.loadDirectorsForFilm(filmsFound);
        return filmsFound;
    }
}
