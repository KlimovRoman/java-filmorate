package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class Film {
    private int id;

    @NotNull
    @NotBlank
    private String name;

    private String description;
    private String releaseDate;
    private Double duration;
    public Film(int id){
        this.id = id;
    }
    public Film(){

    }

}

