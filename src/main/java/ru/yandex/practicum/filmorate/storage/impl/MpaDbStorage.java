package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public List<Mpa> getMpas() {
        String sql = "select * from rating";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name_rating");
        return new Mpa(id, name);
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {
        // полученный фильм пока не обогащен рейтингом
        String sql = "select * from rating where id =?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sql, id);
        if(mpaRows.next()) {
            int mpaId = mpaRows.getInt("id");
            String name = mpaRows.getString("name_rating");
            Mpa mpa = new Mpa(mpaId,name);
            return Optional.of(mpa);
        } else {
            return Optional.empty();
        }
    }
}
