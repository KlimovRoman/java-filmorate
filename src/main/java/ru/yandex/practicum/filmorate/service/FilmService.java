package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage; //поле куда будет передано хранилище через контструктор с помощью зависимостей
    private Comparator<Film> comparatorForTopLikes;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final LocalDate dateForCompare =  LocalDate.parse("1895-12-28",formatter);

    //связали зависимостью  сервис и хранилище
    @Autowired
    public FilmService(FilmStorage filmStorage, Comparator<Film> comparatorForTopLikes) {
        this.filmStorage = filmStorage;
        this.comparatorForTopLikes = comparatorForTopLikes;
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
        return filmStorage.getFilms();
    }

    public void addLike(int filmId, int userLikeId) {
        Film film = getFilmById(filmId);
        if (film != null) {
            film.addLike(userLikeId);
        } else {
            throw new EntityNotFoundException("Фильм не найден в базе!");
        }
    }

    public void delLike(int filmId, int userLikeId) {
        Film film = getFilmById(filmId);
        if (film != null) {
            film.delLike(userLikeId);
        } else {
            throw new EntityNotFoundException("Фильм не найден в базе!");
        }
    }

    public Film getFilmById(int id) {
       return filmStorage.getFilmById(id).orElseThrow(() -> new EntityNotFoundException("Фильм не найден в базе"));
    }

    public List<Film> getTopMostLikedFilms(int topCount) {
        List<Film> filmsToReturn = new ArrayList<>();
        List<Film> allFilms = filmStorage.getFilms();
        allFilms.sort(comparatorForTopLikes);
        if (topCount > allFilms.size()) {
            topCount = allFilms.size(); // на случай если фильмов меньше чем запрошенное кол-во
        }
        for (int i = 0; i < topCount; i++) {
            filmsToReturn.add(allFilms.get(i));
        }
        return filmsToReturn;
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
