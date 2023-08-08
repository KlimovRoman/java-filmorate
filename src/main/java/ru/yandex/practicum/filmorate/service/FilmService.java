package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage; //поле куда будет передано хранилище через контструктор с помощью зависимостей

    //связали зависимостью  сервис и хранилище
    @Autowired
    public FilmService (FilmStorage filmStorage) {
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
}
