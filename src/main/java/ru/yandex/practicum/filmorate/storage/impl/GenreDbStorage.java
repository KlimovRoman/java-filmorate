package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Override
    public void loadGenresForFilm(List<Film> films) {
        List<Integer> ids = new ArrayList<>();
        Map<Integer,Film> filmsMap = new HashMap<>();

        for (Film film: films) {
            filmsMap.put(film.getId(),film);
            ids.add(film.getId()); //создаем список айдишников
        }

        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        String sql = "select * from genre_films gf left join genre g on gf.genre_id = g.id where gf.FILM_ID IN (:ids)";
        List<Genre> genres = namedJdbcTemplate.query(
                sql,
                parameters,
                (rs, rowNum) -> makeGenreForFilm(rs,filmsMap));

    }


    @Override
    public List<Genre> getGenres() {
        String sql = "select * from genre";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }


    @Override
    public Optional<Genre> getGenreById(int id) {
        String sql = "select * from genre where id =?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql, id);
        return genreMapper(genreRows);
    }

    @Override
    public int[] gernesBatchInsert(LinkedHashSet<Genre> genres, int filmId) {
        List<Integer> genreIds = new ArrayList<>();
        for (Genre genre: genres) {
            if (genre != null) {
                genreIds.add(genre.getId());
            }
        }
        log.info("батч инсертов");
        return this.jdbcTemplate.batchUpdate(
                "insert into genre_films(genre_id, film_id) values (?, ?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, genreIds.get(i));
                        ps.setInt(2, filmId);
                    }

                    public int getBatchSize() {
                        return genreIds.size();
                    }
                });
    }

    @Override
    public void delAllGenresFromFilm(int filmId) {
        String sqlQuery = "delete from genre_films where film_id = " + filmId;
        int count =  jdbcTemplate.update(sqlQuery);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name_genre");
        return new Genre(id,name);
    }


    private Genre makeGenreForFilm(ResultSet rs, Map<Integer,Film> filmsMap) throws SQLException {
       // Map<Integer,Film> filmsMap = new HashMap<>();
        int filmId = rs.getInt("film_id");
        int genreId = rs.getInt("genre_id");
        String name = rs.getString("name_genre");
        Genre genre = new Genre(genreId,name);
        if (filmsMap.containsKey(filmId)) {
            filmsMap.get(filmId).getGenres().add(genre);
        }
        return genre;
    }

    private Optional<Genre> genreMapper(SqlRowSet genreRows) {
        if (genreRows.next()) {
            int genreId = genreRows.getInt("id");
            String name = genreRows.getString("name_genre");
            Genre genre = new Genre(genreId,name);
            return Optional.of(genre);
        } else {
            return Optional.empty();
        }
    }



}