package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface EventStorage {
    void addEvent(Event event);

    Event makeEvent(ResultSet rs) throws SQLException;
}
