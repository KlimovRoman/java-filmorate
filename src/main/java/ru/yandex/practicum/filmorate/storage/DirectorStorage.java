package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DirectorStorage {

    public List<Director> getDirectors();

    public Optional<Director> getDirectorById(int id);

     public Director addDirector(Director directorToAdd);

    public Director updDirector(Director directorToUpd);

    public void delDirector(int id);

    public boolean contains(int id);

    public int[] directorBatchInsert(Set<Director> directors, int filmId);

    public void loadDirectorsForFilm(List<Film> films);

    public void delAllDirectorsFromFilm(int filmId);
}
