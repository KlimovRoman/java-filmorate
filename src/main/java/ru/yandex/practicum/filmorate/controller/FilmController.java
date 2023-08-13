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

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void delLike(@PathVariable int id, @PathVariable int userId) {
        filmService.delLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopMostLikedFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getTopMostLikedFilms(count);
    }

}
