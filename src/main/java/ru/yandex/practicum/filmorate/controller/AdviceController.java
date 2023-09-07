package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.AdviceService;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdviceController {
    private final AdviceService adviceService;

    @GetMapping("/users/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable Integer id) {
        log.info("Request has been created to get recommendations");
        return adviceService.getRecommendations(id);
    }
}
