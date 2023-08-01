package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final LocalDate dateForCompare =  LocalDate.parse("1895-12-28",formatter);

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film filmToAdd) {

            releaseDateValid(filmToAdd);
            filmIdCounter++;
            filmToAdd.setId(filmIdCounter);
            films.put(filmIdCounter, filmToAdd);
            log.info("Добавлен фильм с id = {}", filmIdCounter);
            return filmToAdd;
    }

    @PutMapping
    public Film updFilm(@Valid @RequestBody Film filmToUpd) {

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
            log.info("Валидация не пройдена при обновлении фильма");
            throw new ValidationException("Не пройдена валидация");
        }

    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    // методы для валидации
    private void releaseDateValid(Film filmToCheck) {
        LocalDate dateToCheck = filmToCheck.getReleaseDate();
        if (!dateToCheck.isAfter(dateForCompare)) {
            log.info("Валидация не пройдена, дата релиза должна быть после 1895-12-28");
            throw new ValidationException("Не пройдена валидация");
        }
    }
}
