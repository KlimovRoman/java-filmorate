package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Override
    public List<Director> getDirectors() {
        String sql = "select * from director";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));

    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name_director");
        return new Director(id,name);
    }

    @Override
    public Optional<Director> getDirectorById(int id) {
        String sql = "select * from director where id =?";
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet(sql, id);
        return directorMapper(directorRows);
    }

    private Optional<Director> directorMapper(SqlRowSet directorRows) {
        if (directorRows.next()) {
            int id = directorRows.getInt("id");
            String name = directorRows.getString("name_director");
            Director director = new Director(id,name);
            return Optional.of(director);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Director addDirector(Director directorToAdd) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("director")
                .usingColumns("name_director")
                .usingGeneratedKeyColumns("id");
        directorToAdd.setId(simpleJdbcInsert.executeAndReturnKey(directorToAdd.toMap()).intValue());
        return directorToAdd;
    }

    @Override
    public Director updDirector(Director directorToUpd) {
        if (!contains(directorToUpd.getId())) {
            throw new EntityNotFoundException("не найден режиссер с id - " + directorToUpd.getId());
        }
        String sqlQuery = "UPDATE director SET name_director = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, directorToUpd.getName(), directorToUpd.getId());
        return directorToUpd;
    }

    @Override
    public void delDirector(int id){
        String sqlQuery = "delete from director where id = ?";
        int count =  jdbcTemplate.update(sqlQuery, id);
        if (count == 0) {
            throw new  EntityNotFoundException("директор не найден в базе");
        }
    }

    @Override
    public boolean contains(int id) {
        String sqlQuery = "SELECT id FROM director WHERE id = ?";
        return jdbcTemplate.queryForRowSet(sqlQuery, id).next();
    }

    @Override
    public int[] directorBatchInsert(Set<Director> directors, int filmId) {
        List<Integer> directorIds = new ArrayList<>();
        for (Director director: directors) {
            if (director != null) {
                directorIds.add(director.getId());
            }
        }
        log.info("батч инсертов");
        return jdbcTemplate.batchUpdate(
                "insert into director_films(director_id, film_id) values (?, ?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, directorIds.get(i));
                        ps.setInt(2, filmId);
                    }

                    public int getBatchSize() {
                        return directorIds.size();
                    }
                });
    }

    public void loadDirectorsForFilm(List<Film> films) {
        List<Integer> ids = new ArrayList<>();
        Map<Integer,Film> filmsMap = new HashMap<>();

        for (Film film: films) {
            filmsMap.put(film.getId(),film);
            ids.add(film.getId()); //создаем список айдишников
        }

        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        String sql = "select * from director_films df left join director d on df.director_id = d.id where df.FILM_ID IN (:ids)";
        List<Director> directors = namedJdbcTemplate.query(
                sql,
                parameters,
                (rs, rowNum) -> makeDirectorForFilm(rs,filmsMap));
    }

    private Director makeDirectorForFilm(ResultSet rs, Map<Integer,Film> filmsMap) throws SQLException {
        int filmId = rs.getInt("film_id");
        int directorId = rs.getInt("director_id");
        String name = rs.getString("name_director");
        Director director = new Director(directorId,name);
        Film film = filmsMap.get(filmId);
        if (film != null) {
            film.getDirectors().add(director);
        }
        return director;
    }

    public void delAllDirectorsFromFilm(int filmId) {
        String sqlQuery = "delete from director_films where film_id = ?";
        int count =  jdbcTemplate.update(sqlQuery, filmId);
    }
}
