package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.LinkedHashSet;

@Data
public class Film {
    private int id;
    private LinkedHashSet<Genre> genres = new LinkedHashSet<>(); // создать в модели
    private Mpa mpa = new Mpa();
    @NotBlank
    private String name;
    @NotNull @Size(max = 200)
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull @Positive
    private Double duration;

    public Film(int id) {
        this.id = id;
    }

    public Film() {
    }


    public long getLikesCount() {
        return 0;//доделать или удалить
    }
}
