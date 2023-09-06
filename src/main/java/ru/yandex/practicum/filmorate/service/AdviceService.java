package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class AdviceService {
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final UserService userService;

    public List<Film> getRecommendations(final Integer userId) {
        checkUser(userId);
        List<Integer> recommendedFilmsId = filmStorage.getRecommendedFilmsID(userId);
        log.debug("List of recommended films consists of " + recommendedFilmsId.size() + " films");
        return getRecommendedFilms(recommendedFilmsId);
    }

    private void checkUser(final Integer id) {
        try {
            userService.getUserById(id);
        } catch (EntityNotFoundException e) {
            log.debug("User with id = " + id + " hasn't been found");
            throw new EntityNotFoundException("User with id = " + id + " doesn't exist " +
                    AdviceService.class.getSimpleName());
        }
    }

    private List<Film> getRecommendedFilms(List<Integer> recommendedFilmsId) {
        List<Film> recommendedFilms = filmStorage.getRecommendedFilms(recommendedFilmsId);
        genreStorage.loadGenresForFilm(recommendedFilms);
        return recommendedFilms;
    }
}