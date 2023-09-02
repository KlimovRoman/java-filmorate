package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class AdviceService {
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final UserService userService;
    private Map<Integer, List<Integer>> allLikedFilmsId;
    private List<Integer> userLikedFilmsId;
    private Map<Integer, List<Integer>> otherUsersLikedFilmsId;
    private static final int NOT_EXIST_USER_ID = 0;

    public List<Film> getRecommendations(final Integer userId) {
        checkUser(userId);
        Integer idUserWithEqualInterests = getUserIdWithEqualInterests(userId);
        if (idUserWithEqualInterests == NOT_EXIST_USER_ID) {
            log.debug("Nobody has the same interests");
            return Collections.emptyList();
        }
        List<Integer> recommendedFilmsId = otherUsersLikedFilmsId.get(idUserWithEqualInterests)
                .stream()
                .filter(value -> !userLikedFilmsId.contains(value))
                .collect(Collectors.toList());
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
        int length = recommendedFilmsId.size();
        StringBuilder rangeId = new StringBuilder();
        if (length == 0) {
            rangeId.append(")");
            log.debug("Recommended film list is empty");
        }
        for (int i = 0; i < length; i++) {
            if (i != length - 1) {
                rangeId.append(recommendedFilmsId.get(i));
                rangeId.append(", ");
            } else {
                rangeId.append(recommendedFilmsId.get(i));
                rangeId.append(")");
            }
            log.debug("Recommended film list consists " + (length - 1) + " films");
        }
        List<Film> recommendedFilms = filmStorage.getRecommendedFilms(rangeId.toString()).get();
        genreStorage.loadGenresForFilm(recommendedFilms);
        return recommendedFilms;
    }

    private Integer getUserIdWithEqualInterests(final Integer userId) {
        categorizeAllLikedFilms(userId);
        if (otherUsersLikedFilmsId.isEmpty()) {
            log.debug("Nobody liked films");
            return NOT_EXIST_USER_ID;
        } else {
            for (Integer anotherUserId : otherUsersLikedFilmsId.keySet()) {
                return anotherUserId;
            }
        }
        Map<Integer, Integer> identityInterests = countIdentityInterests();
        if (identityInterests.isEmpty()) {
            log.debug("Crossing interests are absent");
            return NOT_EXIST_USER_ID;
        } else {
            return sortIdentityInterests(identityInterests).entrySet().iterator().next().getKey();
        }
    }

    private void categorizeAllLikedFilms(final Integer userId) {
        allLikedFilmsId = filmStorage.getAllLikedFilms().get();
        userLikedFilmsId = allLikedFilmsId.remove(userId);
        otherUsersLikedFilmsId = allLikedFilmsId;
    }

    private Map<Integer, Integer> countIdentityInterests() {
        Map<Integer, Integer> identityCounter = new HashMap<>();
        for (Integer anotherUserId : otherUsersLikedFilmsId.keySet()) {
            int counter = 0;
            for (int likedFilmId : otherUsersLikedFilmsId.get(anotherUserId)) {
                if (userLikedFilmsId.contains(likedFilmId)) {
                    counter++;
                }
            }
            identityCounter.put(anotherUserId, counter);
        }
        return identityCounter;
    }

    private Map<Integer, Integer> sortIdentityInterests(Map<Integer, Integer> identityCounter) {
        return identityCounter.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1,v2) -> v1, HashMap::new));
    }
}