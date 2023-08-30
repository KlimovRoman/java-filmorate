package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.LikeReviewStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final LikeReviewStorage likeReviewStorage;


    public Review create(Review review) {
        return reviewStorage.create(review);
    }

    public Review update(Review review) {
        return reviewStorage.update(review).orElseThrow(() -> new EntityNotFoundException("Отзыв не найден."));
    }


    public void delete(Integer id) {
        reviewStorage.delete(id);
    }


    public Review findById(Integer id) {
        return reviewStorage.findById(id).orElseThrow(() -> new EntityNotFoundException("Отзыв не найден."));
    }

    public List<Review> findAll(Integer filmId, Integer count) {
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