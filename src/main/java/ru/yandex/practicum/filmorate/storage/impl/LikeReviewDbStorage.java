package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.LikeReviewStorage;

@Component
@RequiredArgsConstructor
public class LikeReviewDbStorage implements LikeReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void createLike(int id, int userId) {
        String sqlQuery = "INSERT INTO like_review (review_id, user_id, is_positive) VALUES (?, ?, 1)";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Override
    public void createDislike(int id, int userId) {
        String sqlQuery = "INSERT INTO like_review (review_id, user_id, is_positive) VALUES (?, ?, -1)";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Override
    public void deleteLike(int id, int userId) {
        String sqlQuery = "DELETE FROM like_review WHERE review_id = ? AND user_id = ? AND is_positive = 1";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Override
    public void deleteDislike(int id, int userId) {
        String sqlQuery = "DELETE FROM like_review WHERE review_id = ? AND user_id = ? AND is_positive = -1";
        jdbcTemplate.update(sqlQuery, id, userId);
    }
}
