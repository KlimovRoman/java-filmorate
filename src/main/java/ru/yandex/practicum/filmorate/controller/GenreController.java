package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
    //поле, куда будет передан сервис через контструктор с помощью зависимостей
    // конструктор создан с помощью аннотацией RequiredArgsConstructor
    private final GenreService genreService;

    @GetMapping
    public List<Genre> getGenres() {
        log.info("Запрос жанров /genres");
        return genreService.getGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id) {
        log.info("Запрос жанра по id /genres/{}",id);
        return genreService.getGenreById(id);
    }
}
