package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private HashMap<Integer, Film> films = new HashMap<>();
    private int filmIdCounter = 0;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final LocalDate dateForCompare =  LocalDate.parse("1895-12-28",formatter);

    @Override
    public Film addFilm(Film filmToAdd) {
        releaseDateValid(filmToAdd);
        filmIdCounter++;
        filmToAdd.setId(filmIdCounter);
        films.put(filmIdCounter, filmToAdd);
        log.info("Добавлен фильм с id = {}", filmIdCounter);
        return filmToAdd;
    }

    @Override
    public Film updFilm(Film filmToUpd) {
        releaseDateValid(filmToUpd);
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
    public Film getFilmById(int id) {
        Film film = films.get(id);
        if (film == null) {
            throw new EntityNotFoundException("Фильм не найден в базе");
        } else {
            return films.get(id);
        }
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