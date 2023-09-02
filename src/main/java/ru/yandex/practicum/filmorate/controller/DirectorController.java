package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@Validated
@Slf4j
@RestController
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public List<Director> getDirectors() {
        log.info("Запрос режиссёров /directors");
        return directorService.getDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable int id) {
        log.info("Запрос режиссёра по id /directors/{}",id);
        return directorService.getDirectorById(id);
    }

    @PostMapping
    public Director addDirector(@Valid @RequestBody Director directorToAdd) {
        return directorService.addDirector(directorToAdd);
    }

    @PutMapping
    public Director updDirector(@Valid @RequestBody Director directorToUpd) {
        return directorService.updDirector(directorToUpd);
    }

    @DeleteMapping("/{id}")
    public void delDirector(@PathVariable int id) {
        directorService.delDirector(id);
        return;
    }
}
