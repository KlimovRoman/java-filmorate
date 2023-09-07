package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.constant.EventType;
import ru.yandex.practicum.filmorate.constant.OperationType;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final LikeReviewStorage likeReviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventStorage eventStorage;

    public Review create(Review review) {
        userStorage.checkIdInDatabase(review.getUserId());
        filmStorage.checkIdInDatabase(review.getFilmId());

        Review reviewFromDb = reviewStorage.create(review);
        eventStorage.addEvent(reviewFromDb.getUserId(), reviewFromDb.getReviewId(), OperationType.ADD, EventType.REVIEW);

        return reviewFromDb;
    }

    public Review update(Review review) {
        userStorage.checkIdInDatabase(review.getUserId());
        filmStorage.checkIdInDatabase(review.getFilmId());
        reviewStorage.ensureReviewExists(review.getReviewId());

        Review reviewFromDb = reviewStorage.update(review).get();
        eventStorage.addEvent(reviewFromDb.getUserId(), reviewFromDb.getReviewId(), OperationType.UPDATE, EventType.REVIEW);

        return reviewFromDb;
    }

    public void delete(Integer id) throws EntityNotFoundException {
        reviewStorage.ensureReviewExists(id);

        Review review = reviewStorage.findById(id).get();
        reviewStorage.delete(id);
        eventStorage.addEvent(review.getUserId(), review.getReviewId(), OperationType.REMOVE, EventType.REVIEW);
    }

    public Review findById(Integer id) {
        return reviewStorage.findById(id).orElseThrow(() -> new EntityNotFoundException("Отзыв не найден."));
    }

    public List<Review> findAll(Optional<Integer> filmId, Integer count) {
        return reviewStorage.findAll(filmId, count);
    }

    public void createLike(Integer id, Integer userId) {
        likeReviewStorage.createLike(id, userId);
    }

    public void createDislike(Integer id, Integer userId) {
        likeReviewStorage.createDislike(id, userId);
    }

    public void deleteLike(Integer id, Integer userId) {
        likeReviewStorage.deleteLike(id, userId);
    }

    public void deleteDislike(Integer id, Integer userId) {
        likeReviewStorage.deleteDislike(id, userId);
    }
}