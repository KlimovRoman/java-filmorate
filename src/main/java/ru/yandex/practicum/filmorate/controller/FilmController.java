package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private HashMap<Integer, Film> films = new HashMap<>();
    private int filmIdCounter = 0;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film filmToAdd) {

        if (releaseDateValid(filmToAdd)) {
            filmIdCounter++;
            Film newFilm = new Film(filmIdCounter);
            newFilm.setReleaseDate(filmToAdd.getReleaseDate());
            newFilm.setName(filmToAdd.getName());
            newFilm.setDescription(filmToAdd.getDescription());
            newFilm.setDuration(filmToAdd.getDuration());
            films.put(filmIdCounter, newFilm);
            log.info("Добавлен фильм с id = {}", filmIdCounter);
            return newFilm;
        } else {
            log.info("Валидация не пройдена при добавлени фильма");
            throw new ValidationException("Не пройдена валидация");
        }
    }

    @PutMapping
    public Film updFilm(@Valid @RequestBody Film filmToUpd) {

        if (releaseDateValid(filmToUpd)) {

            final Integer id = filmToUpd.getId();
            if (films.containsKey(id)) {
                Film filmFromHash = films.get(id);
                filmFromHash.setDescription(filmToUpd.getDescription());
                filmFromHash.setName(filmToUpd.getName());
                filmFromHash.setDuration(filmToUpd.getDuration());
                filmFromHash.setReleaseDate(filmToUpd.getReleaseDate());
                log.info("Обновлен фильм с id = {}", id);
                return filmFromHash;
            } else {
                log.info("Фильм не найден");
                throw new EntityNotFoundException("Фильм не найден");
            }
        } else {
            log.info("Валидация не пройдена при обновлении фильма");
            throw new ValidationException("Не пройдена валидация");
        }

    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    // методы для валидации
    private boolean releaseDateValid(Film filmToCheck) {
        LocalDate dateToCheck = LocalDate.parse(filmToCheck.getReleaseDate(),formatter);
        LocalDate dateForCompare =  LocalDate.parse("1895-12-28",formatter);
        if (dateToCheck.isAfter(dateForCompare)) {
            return true;
        } else {
            log.info("Валидация не пройдена, дата релиза должна быть после 1895-12-28");
            return false;
        }
    }
}
