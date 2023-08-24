package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
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
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final LocalDate dateForCompare =  LocalDate.parse("1895-12-28",formatter);

    //связали зависимостью  сервис и хранилище
    @Autowired
    public FilmService(FilmStorage filmStorage, GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.genreStorage = genreStorage;
    }

    public Film addFilm(Film filmToAdd) {
        releaseDateValid(filmToAdd);
        return filmStorage.addFilm(filmToAdd);
    }

    public Film updFilm(Film filmToUpd) {
        releaseDateValid(filmToUpd);
         return filmStorage.updFilm(filmToUpd);
    }

    public List<Film> getFilms() {
        List<Film> tempFilms =  filmStorage.getFilms();
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
       return film;
    }

    public List<Film> getTopMostLikedFilms(int topCount) {
        log.info("отработка Сервиса POPULAR count " + topCount);
        List<Film> listForGenresUpd = filmStorage.getTopMostLikedFilms(topCount);
        genreStorage.loadGenresForFilm(listForGenresUpd);
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
}
