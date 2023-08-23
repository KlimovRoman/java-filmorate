package ru.yandex.practicum.filmorate.storage.impl;


import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Slf4j
//@Primary
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> films = new HashMap<>();
    private int filmIdCounter = 0;

    @Override
    public Film addFilm(Film filmToAdd) {
        filmIdCounter++;
        filmToAdd.setId(filmIdCounter);
        films.put(filmIdCounter, filmToAdd);
        log.info("Добавлен фильм с id = {}", filmIdCounter);
        return filmToAdd;
    }

    @Override
    public Film updFilm(Film filmToUpd) {
        final Integer id = filmToUpd.getId();
        Film film = films.get(id);
        if (film != null) {
            film.setName(filmToUpd.getName());
            film.setDescription(filmToUpd.getDescription());
            film.setDuration(filmToUpd.getDuration());
            film.setReleaseDate(filmToUpd.getReleaseDate());
            log.info("Обновлен фильм с id = {}", id);
            return film;
        } else {
            log.info("Фильм не найден в базе");
            throw new EntityNotFoundException("Фильм не найден в базе");
        }
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void addLike(int i, int u){

    }

    @Override
    public void delLike(int filmId, int userLikeId) {

    }

    public List<Film> getTopMostLikedFilms(int topCount){
        return null;
    }
}