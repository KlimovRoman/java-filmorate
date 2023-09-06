package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.constant.EventType;
import ru.yandex.practicum.filmorate.constant.OperationType;

@Data
@Builder
public class Event {
    private int eventId;
    private int userId;

    private int entityId;

    private int entityUserId;
    private int entityFilmId;
    private int entityReviewId;

    private long timestamp;
    private OperationType operation;
    private EventType eventType;
}
