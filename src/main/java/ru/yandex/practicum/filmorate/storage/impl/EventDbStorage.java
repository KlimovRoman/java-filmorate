package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.constant.EventType;
import ru.yandex.practicum.filmorate.constant.OperationType;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Component
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addEvent(int userId, int selectedEntityId, OperationType operationType, EventType eventType) {
        String sqlQueryOnCreateEvent = "insert into events(" +
                "user_id, " +
                "entity_id, " +
                "time, " +
                "operation_type, " +
                "event_type) " +

                "values (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQueryOnCreateEvent, new String[]{"id"});

            stmt.setLong(1, userId);
            stmt.setLong(2, selectedEntityId);
            stmt.setTimestamp(3, Timestamp.from(Instant.now()));
            stmt.setString(4, operationType.toString());
            stmt.setString(5, eventType.toString());

            return stmt;
        }, keyHolder);
    }

    @Override
    public List<Event> getFeedByUserId(int userId) {
        String sqlQuery =
                "select * " +
                        "from events " +
                        "where user_id = ? " +
                        "order by user_id; ";
        List<Event> events = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeEvent(rs), userId);
        return events;
    }

    @Override
    public Event makeEvent(ResultSet rs) throws SQLException {
        Event event = Event.builder()
                .eventId(rs.getInt("id"))
                .userId(rs.getInt("user_id"))
                .timestamp(rs.getTimestamp("time").toInstant().toEpochMilli())
                .operation(OperationType.valueOf(rs.getString("operation_type")))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .build();

        if (rs.getString("event_type").equals("FRIEND")) {
            int entityUserId = rs.getInt("entity_id");
            event.setEntityUserId(entityUserId);
            event.setEntityId(entityUserId);
        } else if (rs.getString("event_type").equals("LIKE")) {
            int entityFilmId = rs.getInt("entity_id");
            event.setEntityFilmId(entityFilmId);
            event.setEntityId(entityFilmId);
        } else {
            int entityReviewId = rs.getInt("entity_id");
            event.setEntityReviewId(entityReviewId);
            event.setEntityId(entityReviewId);
        }

        return event;
    }
}
