package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private HashMap<Integer, Film> films = new HashMap<>();
    private int filmIdCounter = 0;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @PostMapping
    public void addFilm(@Valid @RequestBody Film filmToAdd){

        if(nameIsEmptyValid(filmToAdd) && descLen200Valid(filmToAdd) &&
                                    releaseDateValid(filmToAdd) && durationValid(filmToAdd)){
            filmIdCounter++;
            Film newFilm = new Film(filmIdCounter);
            newFilm.setReleaseDate(filmToAdd.getReleaseDate());
            newFilm.setName(filmToAdd.getName());
            newFilm.setDescription(filmToAdd.getDescription());
            newFilm.setDuration(filmToAdd.getDuration());
            films.put(filmIdCounter, newFilm);
            log.info("Добавлен фильм с id=" + filmIdCounter);
        } else {
            log.error("Валидация не пройдена при добавлени фильма");
            throw new ValidationException("Не пройден один валидаторов");
        }
    }

    @PutMapping
    public void updFilm(@Valid @RequestBody Film filmToUpd){

        if(nameIsEmptyValid(filmToUpd) && descLen200Valid(filmToUpd) &&
                releaseDateValid(filmToUpd) && durationValid(filmToUpd)){

            final Integer id = filmToUpd.getId();
            if (films.containsKey(id)){
                Film filmFromHash = films.get(id);
                filmFromHash.setDescription(filmToUpd.getDescription());
                filmFromHash.setName(filmToUpd.getName());
                filmFromHash.setDuration(filmToUpd.getDuration());
                filmFromHash.setReleaseDate(filmToUpd.getReleaseDate());
                log.info("Обновлен фильм с id=" + id);
            } else {
                log.error("Фильм не найден");
                throw new FilmNotFoundException("Фильм не найден");
            }
        } else {
            log.error("Валидация не пройдена при обновлении фильма");
            throw new ValidationException("Не пройден один валидаторов");
        }

    }

    @GetMapping
    public List<Film> getFilms(){
        return new ArrayList<>(films.values());
    }

    // методы для валидации

    private boolean nameIsEmptyValid ( Film filmToCheck ){
        if (filmToCheck.getName()!=null && !filmToCheck.getName().isBlank() && !filmToCheck.getName().isEmpty()){
            return true;
        }else {
            log.error("Валидация не пройдена, имя пустое");
            return false;
        }
    }

    private boolean descLen200Valid ( Film filmToCheck ){
        if (filmToCheck.getDescription().length() <= 200){
            return true;
        } else{
            log.error("Валидация не пройдена, описание более 200 символов");
            return false;
        }
    }

    private boolean releaseDateValid ( Film filmToCheck ){
        LocalDate dateToCheck = LocalDate.parse(filmToCheck.getReleaseDate(),formatter);
        LocalDate dateForCompare =  LocalDate.parse("1895-12-28",formatter);
        if(dateToCheck.isAfter(dateForCompare)){
            return true;
        } else {
            log.error("Валидация не пройдена, дата релиза должна быть после 1895-12-28");
            return false;
        }
    }

    private boolean durationValid ( Film filmToCheck){
        if (filmToCheck.getDuration() > 0){
            return true;
        } else {
            log.error("Валидация не пройдена,длительность фильма должна быть >0");
            return false;
        }
    }

}