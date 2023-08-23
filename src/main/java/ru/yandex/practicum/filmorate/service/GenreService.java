package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class GenreService {
    private final GenreStorage genreStorage; //поле которое хранит dao и внедряется  через  RequiredArgsConstructor

    public List<Genre> getGenres() {
        return genreStorage.getGenres();
    }

    public Genre getGenreById(int id) {
        return genreStorage.getGenreById(id).orElseThrow(() -> new EntityNotFoundException("Жанр не найден в базе"));
    }

}
