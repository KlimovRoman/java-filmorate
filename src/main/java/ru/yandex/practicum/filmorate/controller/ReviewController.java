package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService service;

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        return service.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        return service.update(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    public Review findById(@PathVariable Integer id) {
        return service.findById(id);
    }

    @GetMapping()
    public List<Review> findAll(@RequestParam(defaultValue = "0", required = false) @Positive Integer filmId, @RequestParam(defaultValue = "10", required = false) @Positive Integer count) {
        return service.findAll(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void createLike(@PathVariable Integer id, @PathVariable Integer userId) {
        service.createLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void createDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        service.createDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        service.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        service.deleteDislike(id, userId);
    }

}
