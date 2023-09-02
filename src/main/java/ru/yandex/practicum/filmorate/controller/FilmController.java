package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RequiredArgsConstructor
@Validated
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;



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
    public List<Film> getTopMostLikedFilms(@Positive @RequestParam(defaultValue = "10") int count) {
        return filmService.getTopMostLikedFilms(count);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirectors(@PathVariable int directorId, @RequestParam String sortBy) {
        return filmService.getFilmsByDirectors(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> getFilmsBySearch(@RequestParam String query, @RequestParam String[] by) {
        return filmService.getFilmsBySearch(query, by);
    }
}
