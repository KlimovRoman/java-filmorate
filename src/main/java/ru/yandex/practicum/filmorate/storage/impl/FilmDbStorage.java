package ru.yandex.practicum.filmorate.storage.impl;

import ru.yandex.practicum.filmorate.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.constant.EventType;
import ru.yandex.practicum.filmorate.constant.OperationType;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Primary
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final EventStorage eventStorage;

    private static final String SELECT_RECOMMENDED_FILMS = "SELECT f.*, r.*, " +
            "FROM films f INNER JOIN rating r ON f.rating_id = r.mpa_id " +
            "WHERE f.id IN (";
    private static final String SELECT_ALL_LIKED_FILMS_ID = "SELECT * FROM likes";

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, EventStorage eventStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.eventStorage = eventStorage;
    }

    @Override
    public Film addFilm(Film filmToAdd) {
        String sqlQuery = "insert into films(rating_id, name, description, release_date, duration) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setInt(1, filmToAdd.getMpa().getId());
            stmt.setString(2, filmToAdd.getName());
            stmt.setString(3, filmToAdd.getDescription());
            stmt.setDate(4, Date.valueOf(filmToAdd.getReleaseDate()));
            stmt.setDouble(5, filmToAdd.getDuration());
            return stmt;
        }, keyHolder);
        int newFilmID = keyHolder.getKey().intValue();
        filmToAdd.setId(newFilmID);
        return filmToAdd;
    }

    @Override
    public Film updFilm(Film filmToUpd) {
        int filmId = filmToUpd.getId();
        getFilmById(filmToUpd.getId()).orElseThrow(() -> new EntityNotFoundException("Фильм, который необходимо обновить не найден в базе"));
        String sqlQuery = "update films set " +
                "rating_id = ?, name = ?, description = ?, release_date = ?, duration = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery,
                filmToUpd.getMpa().getId(),
                filmToUpd.getName(),
                filmToUpd.getDescription(),
                Date.valueOf(filmToUpd.getReleaseDate()),
                filmToUpd.getDuration(),
                filmToUpd.getId());
        return filmToUpd;
    }

    @Override
    public List<Film> getFilms() {
        // полученные фильмы не обогащены жанрами, будут обогащены в сервисе
        String sql = "select * from films f join rating r on f.rating_id = r.mpa_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        // полученный фильм пока не обогащен жанрами
        String sql = "select * from films f join rating r on f.rating_id = r.mpa_id where f.id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, id);
        return filmMapper(filmRows);
    }

    @Override
    public void addLike(int filmId, int userLikeId) {
        if (!isContainsLike(filmId, userLikeId)) {

            String sqlQuery = "insert into likes(film_id, user_id) " +
                    "values (?, ?)";
            jdbcTemplate.update(sqlQuery, filmId, userLikeId);

        }
        Event event = Event.builder()
                .userId(userLikeId)
                .entityFilmId(filmId)
                .entityId(filmId)
                .timestamp(Instant.now().toEpochMilli())
                .operation(OperationType.ADD)
                .eventType(EventType.LIKE)
                .build();
        eventStorage.addEvent(event);
    }

    @Override
    public void delLike(int filmId, int userLikeId) {
        String sqlQuery = "delete from likes where film_id = " + filmId + " and user_id = " + userLikeId;
        int count = jdbcTemplate.update(sqlQuery);
        if (count == 0) {
            throw new EntityNotFoundException("Фильм не найден в базе");
        }

        Event event = Event.builder()
                .userId(userLikeId)
                .entityFilmId(filmId)
                .entityId(filmId)
                .timestamp(Instant.now().toEpochMilli())
                .operation(OperationType.REMOVE)
                .eventType(EventType.LIKE)
                .build();
        eventStorage.addEvent(event);
    }

    @Override
    public List<Film> getTopMostLikedFilms(int topCount, Integer year) {
        String sql;

        String sqlStart = "select " +
                "f.id," +
                "f.rating_id," +
                "f.name," +
                "f.description," +
                "f.release_date," +
                "f.duration," +
                "r.name_rating," +
                "r.mpa_id, " +
                "count(user_id), " +
                "df.director_id as director_id, " +
                "d.name_director as name_director " +

                "from films f  " +

                "left join likes l on l.film_id = f.id " +
                "left join rating r on f.rating_id = r.mpa_id " +
                "left join director_films df on f.id = df.film_id " +
                "left join director d on df.director_id = d.id ";
        String sqlFinish =
                "group by f.id " +
                        "order by count(user_id) " +
                        "desc limit ?;";

        if (year != null) {
            String sqlHaveRequiredYear = "where EXTRACT (year FROM CAST (f.release_date AS date))  = ?";
            sql = String.join(" ", sqlStart, sqlHaveRequiredYear, sqlFinish);

            log.info("выборка популярных фильмов влючает выбранный год: " + year);
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilmWithDirector(rs), year, topCount);

        } else {
            sql = String.join(" ", sqlStart, sqlFinish);
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilmWithDirector(rs), topCount);
        }
    }

    @Override
    public Map<Integer, List<Integer>> getAllLikedFilms() {
        Map<Integer, List<Integer>> allLikedFilms = new HashMap<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SELECT_ALL_LIKED_FILMS_ID);
        while (rowSet.next()) {
            Integer userId = Integer.parseInt(Objects.requireNonNull(rowSet.getString("user_id")));
            Integer filmId = Integer.parseInt(Objects.requireNonNull(rowSet.getString("film_id")));
            allLikedFilms.computeIfAbsent(userId, key -> new ArrayList<>()).add(filmId);
        }
        return allLikedFilms;
    }

    @Override
    public List<Film> getRecommendedFilms(List<Integer> recommendedFilmsId) {
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
        return jdbcTemplate.queryForStream(SELECT_RECOMMENDED_FILMS + rangeId,
                (rs, rowNum) -> makeFilm(rs)).collect(Collectors.toList());
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        //реализация фичи в рамках ГП (12 спринт)
        String sql = "\n" +
                "\tSELECT f.ID,\n" +
                "\t\tf.RATING_ID,\n" +
                "\t\tf.NAME ,\n" +
                "\t\tf.DESCRIPTION,\n" +
                "\t\tf.RELEASE_DATE,\n" +
                "\t\tf.DURATION, \n" +
                "\t\tr.NAME_RATING, \n" +
                "\t\tr.MPA_ID, \n" +
                "\t\tcount(li.USER_ID)\n" +
                "\tFROM FILMS f \n" +
                "\tLEFT JOIN rating r on f.rating_id = r.mpa_id \n" +
                "\tLEFT JOIN likes li ON li.FILM_ID =f.ID\n" +
                "\tWHERE f.ID IN (\n" +
                "\t\tSELECT l.FILM_ID \n" +
                "\t\tFROM LIKES l WHERE L.USER_ID = 1 \n" +
                "\t\tINTERSECT\n" +
                "\t\tSELECT l.FILM_ID \n" +
                "\t\tFROM LIKES l WHERE L.USER_ID = 2 \n" +
                "\t) \n" +
                "\tGROUP BY  f.ID,\n" +
                "\t\tf.RATING_ID,\n" +
                "\t\tf.NAME ,\n" +
                "\t\tf.DESCRIPTION,\n" +
                "\t\tf.RELEASE_DATE,\n" +
                "\t\tf.DURATION,\n" +
                "\t\tr.NAME_RATING, \n" +
                "\t\tr.MPA_ID\n" +
                "\torder by count(user_id) desc";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public void delFilmById(int filmId) {
        String sqlQuery = "DELETE FROM films  WHERE ID = ?";
        int count = jdbcTemplate.update(sqlQuery, filmId);
        if (count == 0) {
            throw new EntityNotFoundException("Фильм не найден в базе (удаление не прошло)");
        }
    }

    @Override
    public List<Film> getFilmsByDirectors(int directorId, String sortBy) {
        String sql = "select f.id , f.rating_id , f.name , f.description , f.release_date," +
                " f.duration, r.name_rating, r.mpa_id, count(l.user_id) as total_likes from films" +
                " as f left join rating r on f.rating_id = r.mpa_id " +
                "left join likes as l on f.id = l.film_id where " +
                " f.id in (select film_id from director_films join director where director_id = ?) " +
                " group by f.id order by " + sortBy;
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), directorId);
    }

    @Override
    public List<Film> getFilmsBySearch(String query, List<FilmSearchBy> by) {
        String fullSort = "";
        for (FilmSearchBy sortPart : by) {
            if (!fullSort.isBlank()) {
                fullSort = fullSort + " or ";
            }
            if (sortPart.equals(FilmSearchBy.director)) {
                fullSort = fullSort + " f.id in (select df.film_id from director_films as df join director as d" +
                        " on df.director_id = d.id where lower(d.name_director) like '%" + query.toLowerCase() + "%') ";
            } else if (sortPart.equals(FilmSearchBy.title)) {
                fullSort = fullSort + " lower(f.name) like '%" + query.toLowerCase() + "%' ";
            } else {
                throw new ValidationException("часть запроса by ошибочна - " + sortPart);
            }
        }
        String sql = "select f.id , f.rating_id , f.name , f.description , f.release_date," +
                " f.duration, r.name_rating, r.mpa_id, count(l.user_id) as total_likes from films" +
                " as f left join rating r on f.rating_id = r.mpa_id " +
                "left join likes as l on f.id = l.film_id " +
                "where " + fullSort + " group by f.id order by total_likes desc";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    private boolean isContainsLike(int filmId, int userId) {
        String sql = "select film_id from likes where film_id = ? and user_id =?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, filmId, userId);

        if (sqlRowSet.next()) {
            return true;
        }
        return false;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setDescription(rs.getString("description"));
        film.setName(rs.getString("name"));
        film.setDuration(rs.getDouble("duration"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("name_rating")));
        film.setDirectors(new HashSet<>());
        return film;
    }

    private Film makeFilmWithDirector(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setDescription(rs.getString("description"));
        film.setName(rs.getString("name"));
        film.setDuration(rs.getDouble("duration"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("name_rating")));

        Director director = new Director(rs.getInt("director_id"), rs.getString("name_director"));
        if (director.getId() != 0) {
            Set<Director> directors = new HashSet<>();
            directors.add(director);
            film.setDirectors(directors);
        } else {
            film.setDirectors(new HashSet<>());
        }

        return film;
    }

    private Optional<Film> filmMapper(SqlRowSet filmRows) {
        if (filmRows.next()) {
            Film film = new Film();
            film.setId(filmRows.getInt("id"));
            film.setDescription(filmRows.getString("description"));
            film.setName(filmRows.getString("name"));
            film.setDuration(filmRows.getDouble("duration"));
            film.setReleaseDate(filmRows.getDate("release_date").toLocalDate());
            film.setMpa(new Mpa(filmRows.getInt("mpa_id"), filmRows.getString("name_rating")));
            film.setDirectors(new HashSet<>());
            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }
}