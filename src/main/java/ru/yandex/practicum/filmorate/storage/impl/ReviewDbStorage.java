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
                .usingGeneratedKeyColumns("review_id");
        review.setReviewId(simpleJdbcInsert.executeAndReturnKey(toMap(review)).intValue());
        return review;
    }

    @Override
    public Optional<Review> update(Review review) {
        String sqlQuery = "UPDATE REVIEWS SET CONTENT = ?, IS_POSITIVE = ?" +
                " WHERE REVIEW_ID = ?";

        int result = jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        if (result == 0)
            return Optional.empty();
        return findById(review.getReviewId());

    }

    @Override
    public void delete(int id) {
        String sql =
                "DELETE " +
                        "FROM REVIEWS " +
                        "WHERE REVIEW_ID = ?";

        int result = jdbcTemplate.update(sql, id);
        if (result == 1)
            log.info("Удалён отзыв id {}", id);
        else
            throw new EntityNotFoundException("Отзыв для удаления не найден.");
    }

    @Override
    public Optional<Review> findById(int id) {
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet("SELECT r.*, " +
                "(COUNT(LRT.USER_ID) - COUNT(LRF.USER_ID)) AS USE " +
                "FROM REVIEWS AS r " +
                "LEFT JOIN (SELECT * FROM LIKE_REVIEW WHERE IS_POSITIVE = true) LRT on r.REVIEW_ID = LRT.REVIEW_ID " +
                "LEFT JOIN (SELECT * FROM LIKE_REVIEW WHERE IS_POSITIVE = false) LRF on r.REVIEW_ID = LRF.REVIEW_ID " +
                " WHERE r.REVIEW_ID = ? GROUP BY r.REVIEW_ID", id);

        if (reviewRows.next()) {
            return Optional.of(reviewRows(reviewRows));
        } else log.info("Фильм с идентификатором {} не найден.", id);
        return Optional.empty();
    }

    @Override
    public List<Review> findAll(int filmId, int count) {
        String where = "";
        if (filmId != 0)
            where = "WHERE FILM_ID = " + filmId;

        String sql = "SELECT r.*, (COUNT(LRT.USER_ID) - COUNT(LRF.USER_ID)) AS USE " +
                "FROM REVIEWS AS r " +
                "LEFT JOIN (SELECT * FROM LIKE_REVIEW WHERE IS_POSITIVE = true) LRT on r.REVIEW_ID = LRT.REVIEW_ID " +
                "LEFT JOIN (SELECT * FROM LIKE_REVIEW WHERE IS_POSITIVE = false) LRF on r.REVIEW_ID = LRF.REVIEW_ID " +
                where +
                " GROUP BY r.REVIEW_ID " +
                " ORDER BY COUNT(LRT.USER_ID) - COUNT(LRF.USER_ID) DESC" +
                " LIMIT " + count;

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs));
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        return new Review(rs.getInt("REVIEW_ID"),
                rs.getString("CONTENT"),
                rs.getBoolean("IS_POSITIVE"),
                rs.getInt("USER_ID"),
                rs.getInt("FILM_ID"),
                rs.getInt("USE"));
    }

    private Review reviewRows(SqlRowSet rs) {
        return new Review(rs.getInt("REVIEW_ID"),
                rs.getString("CONTENT"),
                rs.getBoolean("IS_POSITIVE"),
                rs.getInt("USER_ID"),
                rs.getInt("FILM_ID"),
                rs.getInt("USE"));
    }


    private void ensureUserExists(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * " +
                "FROM USERS " +
                "WHERE ID = ?", id);

        if (!userRows.next()) {
            log.error("Пользователь с идентификатором {} не найден.", id);
            throw new EntityNotFoundException("Пользователь не найден.");
        }
    }

    private void ensureFilmExists(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * " +
                "FROM FILMS " +
                "WHERE ID = ?", id);

        if (!filmRows.next()) {
            log.error("Фильм с идентификатором {} не найден.", id);
            throw new EntityNotFoundException("Фильм не найден.");
        }
    }

    private Map<String, Object> toMap(final Review review) {
        Map<String, Object> values = new HashMap<>();

        values.put("content", review.getContent());
        values.put("is_positive", review.getIsPositive());
        values.put("user_id", review.getUserId());
        values.put("film_id", review.getFilmId());

        return values;
    }

}
