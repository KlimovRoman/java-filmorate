package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DirectorService {

    private final DirectorStorage directorStorage; //поле которое хранит dao и внедряется  через  RequiredArgsConstructor

    public List<Director> getDirectors() {
        return directorStorage.getDirectors();
    }

    public Director getDirectorById(int id) {
        return directorStorage.getDirectorById(id).orElseThrow(() -> new EntityNotFoundException("Режиссер не найден в базе"));
    }

    public Director addDirector(Director directorToAdd) {
        return directorStorage.addDirector(directorToAdd);
    }

    public Director updDirector(Director directorToUpd) {
        return directorStorage.updDirector(directorToUpd);
    }

    public void delDirector(int id) {
        directorStorage.delDirector(id);
    }

}
