package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        for (Film film: films) {
            ids.add(film.getId()); //создаем список айдишников
        }

        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        String sql = "select * from genre_films gf left join genre g on gf.genre_id = g.id where id IN (:ids)";
        List<Genre> genres = namedJdbcTemplate.query(
                sql,
                parameters,
                (rs, rowNum) -> makeGenreForFilm(rs,films));

    }

    @Override
    public void loadGenresForOneFilm(Film film) {
        String sql = "select * from genre_films gf left join genre g on gf.genre_id = g.id where film_id = ?";
        List<Genre> genresForOneFilm = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs),film.getId());
        for (Genre genre: genresForOneFilm) {
            film.getGenres().add(genre);
        }
    }


    private Genre makeGenreForFilm(ResultSet rs, List<Film> films) throws SQLException {
        int filmId = rs.getInt("film_id");
        int genreId = rs.getInt("genre_id");
        String name = rs.getString("name_genre");
        Genre genre = new Genre(genreId,name);
        if (films.contains(filmId)) {
            films.get(filmId).getGenres().add(genre);
        }
        return genre;
    }

    @Override
    public List<Genre> getGenres() {
        String sql = "select * from genre";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name_genre");
        return new Genre(id,name);
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        String sql = "select * from genre where id =?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql, id);
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