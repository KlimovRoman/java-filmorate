package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.constant.EventType;
import ru.yandex.practicum.filmorate.constant.OperationType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Event {
    @NotNull
    private int eventId;
    @NotNull
    private int userId;

    @NotNull
    private int entityId;

    private int entityUserId;
    private int entityFilmId;
    private int entityReviewId;

    @NotNull
    private long timestamp;
    @NotBlank
    private OperationType operation;
    @NotBlank
    private EventType eventType;
}
