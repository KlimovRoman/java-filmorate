package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MpaService {
    private final MpaStorage mpaStorage; //поле которое хранит dao и внедряется  через  RequiredArgsConstructor

    public List<Mpa> getMpas() {
        return mpaStorage.getMpas();
    }

    public Mpa getMpaById(int id) {
        return mpaStorage.getMpaById(id).orElseThrow(() -> new EntityNotFoundException("MPA рейтинг не найден в базе"));
    }
}
