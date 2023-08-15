package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private HashMap<Integer, Film> films = new HashMap<>();
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

}