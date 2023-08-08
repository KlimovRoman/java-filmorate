package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
         this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film filmToAdd) {
        return filmService.addFilm(filmToAdd);
    }

    @PutMapping
    public Film updFilm(@Valid @RequestBody Film filmToUpd) {
        return filmService.updFilm(filmToUpd);
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilms();
    }
}
