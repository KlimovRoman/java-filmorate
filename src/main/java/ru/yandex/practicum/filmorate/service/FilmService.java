package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.LikeComparator;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage; //поле куда будет передано хранилище через контструктор с помощью зависимостей
    private Comparator<Film> comparatorForTopLikes = new LikeComparator();

    //связали зависимостью  сервис и хранилище
    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film filmToAdd) {
        return filmStorage.addFilm(filmToAdd);
    }

    public Film updFilm(Film filmToUpd) {
       return filmStorage.updFilm(filmToUpd);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public void addLike(int filmId, int userLikeId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film != null) {
            film.addLike(userLikeId);
        } else {
            throw new EntityNotFoundException("Фильм не найден в базе!");
        }
    }

    public void delLike(int filmId, int userLikeId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film != null) {
            film.delLike(userLikeId);
        } else {
            throw new EntityNotFoundException("Фильм не найден в базе!");
        }
    }

    public Film getFilmById(int id) {
       return filmStorage.getFilmById(id);
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
}
