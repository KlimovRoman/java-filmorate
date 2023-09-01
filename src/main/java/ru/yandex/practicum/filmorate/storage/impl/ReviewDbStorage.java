package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {
        ensureUserExists(review.getUserId());
        ensureFilmExists(review.getFilmId());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("id");
        review.setReviewId(simpleJdbcInsert.executeAndReturnKey(toMap(review)).intValue());
        return review;
    }

    @Override
    public Optional<Review> update(Review review) {
        ensureUserExists(review.getUserId());
        ensureFilmExists(review.getFilmId());

        String sqlQuery = "UPDATE REVIEWS SET CONTENT = ?, IS_POSITIVE = ? WHERE ID = ?";

        int result = jdbcTemplate.update(sqlQuery, review.getContent(), review.getIsPositive(), review.getReviewId());
        if (result == 0) {
            return Optional.empty();
        }

        return findById(review.getReviewId());
    }

    @Override
    public void delete(int id) {
        String sqlQuery = "DELETE FROM REVIEWS WHERE ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Optional<Review> findById(int id) {
        ensureReviewExists(id);

        String sqlQuery = "SELECT r.*, " +
                "(COUNT(LRT.USER_ID) - COUNT(LRF.USER_ID)) AS USE " +
                "FROM REVIEWS AS r " +
                "LEFT JOIN (SELECT * FROM LIKE_REVIEW WHERE IS_POSITIVE = true) LRT on r.ID = LRT.REVIEW_ID " +
                "LEFT JOIN (SELECT * FROM LIKE_REVIEW WHERE IS_POSITIVE = false) LRF on r.ID = LRF.REVIEW_ID " +
                " WHERE r.ID = ? GROUP BY r.ID";

        Review review = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, id);
        return Optional.ofNullable(review);
    }

    @Override
    public List<Review> findAll(int filmId, int count) {
        String where = "";
        if (filmId != 0)
            where = "WHERE FILM_ID = " + filmId;

        String sql = "SELECT r.*, (COUNT(LRT.USER_ID) - COUNT(LRF.USER_ID)) AS USE " +
                "FROM REVIEWS AS r " +
                "LEFT JOIN (SELECT * FROM LIKE_REVIEW WHERE IS_POSITIVE = true) LRT on r.ID = LRT.REVIEW_ID " +
                "LEFT JOIN (SELECT * FROM LIKE_REVIEW WHERE IS_POSITIVE = false) LRF on r.ID = LRF.REVIEW_ID " +
                where +
                " GROUP BY r.ID " +
                " ORDER BY COUNT(LRT.USER_ID) - COUNT(LRF.USER_ID) DESC" +
                " LIMIT " + count;

        return jdbcTemplate.query(sql, this::mapRowToReview);
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getInt("id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .userId(resultSet.getInt("user_id"))
                .filmId(resultSet.getInt("film_id"))
                .useful(resultSet.getInt("use"))
                .build();
    }

    private Map<String, Object> toMap(final Review review) {
        Map<String, Object> values = new HashMap<>();

        values.put("content", review.getContent());
        values.put("is_positive", review.getIsPositive());
        values.put("user_id", review.getUserId());
        values.put("film_id", review.getFilmId());

        return values;
    }

    private void ensureUserExists(int id) {
        String sqlQuery = "SELECT * FROM USERS WHERE ID = ?";
        ensureEntityExists(sqlQuery, "Пользователь", id);
    }

    private void ensureFilmExists(int id) {
        String sqlQuery = "SELECT * FROM FILMS WHERE ID = ?";
        ensureEntityExists(sqlQuery, "Фильм", id);
    }

    private void ensureReviewExists(int id) {
        String sqlQuery = "SELECT * FROM REVIEWS WHERE ID = ?";
        ensureEntityExists(sqlQuery, "Отзыв", id);
    }

    private void ensureEntityExists(String sqlQuery, String name, int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if (!filmRows.next()) {
            String message = "Сущность \"" + name + "\" с идентификатором " + id + " не найдена.";
            log.error(message);
            throw new EntityNotFoundException(message);
        }
    }
}
