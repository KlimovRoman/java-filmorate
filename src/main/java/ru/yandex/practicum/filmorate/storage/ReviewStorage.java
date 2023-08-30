package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Review create(Review review);

    Optional<Review> update(Review review);

    void delete(int id);

    Optional<Review> findById(int id);

    List<Review> findAll(int filmId, int count);
}