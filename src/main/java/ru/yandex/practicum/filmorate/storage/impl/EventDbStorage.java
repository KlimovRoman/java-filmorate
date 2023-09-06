package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.constant.EventType;
import ru.yandex.practicum.filmorate.constant.OperationType;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
