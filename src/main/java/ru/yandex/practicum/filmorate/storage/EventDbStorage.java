package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface EventDbStorage {
    List<Event> getFeedByUserId(int userId);

    Event makeEvent(ResultSet rs) throws SQLException;
}
