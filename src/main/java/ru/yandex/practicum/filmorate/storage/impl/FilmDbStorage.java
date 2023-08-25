package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


@Slf4j
@Primary
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage  genreStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage  genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
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
        int newFilmID =  keyHolder.getKey().intValue();
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
        String sqlQuery = "insert into likes(film_id, user_id) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userLikeId);
    }

    @Override
    public void delLike(int filmId, int userLikeId) {
       String sqlQuery = "delete from likes where film_id = " + filmId + " and user_id = " + userLikeId;
       int count =  jdbcTemplate.update(sqlQuery);
       if (count == 0) {
           throw new  EntityNotFoundException("Фильм не найден в базе");
       }
    }

    @Override
    public List<Film> getTopMostLikedFilms(int topCount) {
        String sql = "select f.id,f.rating_id,f.name,f.description,f.release_date,f.duration,r.name_rating,r.mpa_id, count(user_id) from films f  left join likes l on l.film_id = f.id left join  rating r on f.rating_id = r.mpa_id group by f.id,f.rating_id,f.name,f.description,f.release_date,f.duration,r.name_rating,r.mpa_id order by count(user_id) desc limit ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs),topCount);
    }


    private Film makeFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setDescription(rs.getString("description"));
        film.setName(rs.getString("name"));
        film.setDuration(rs.getDouble("duration"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("name_rating")));
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
            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }
}