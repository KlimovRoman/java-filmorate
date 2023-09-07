package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.constant.EventType;
import ru.yandex.practicum.filmorate.constant.OperationType;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface EventStorage {
    void addEvent(int userId, int selectedEntityId, OperationType operationType, EventType eventType);

    List<Event> getFeedByUserId(int userId);

    Event makeEvent(ResultSet rs) throws SQLException;
}
