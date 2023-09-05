package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class Film {
    private int id;
    private LinkedHashSet<Genre> genres = new LinkedHashSet<>(); // создать в модели
    @NotNull
    private Mpa mpa; // = new Mpa(); // без этого ломается все
    @NotBlank
    private String name;
    @NotNull @Size(max = 200)
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull @Positive
    private Double duration;

    private Set<Director> directors = new HashSet<>();

    public Film(int id) {
        this.id = id;
    }

    public Film() {
    }
}