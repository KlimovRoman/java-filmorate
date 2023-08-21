package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;
    @JsonIgnore
    private Set<Integer> likes = new HashSet<>();
    @NotBlank
    private String name;
    @Size(max = 200)
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

    public void addLike(int userId) {
        likes.add(userId);
    }

    public void delLike(int userId) {
        if (!likes.remove(userId)) { //Вернет boolean в зависимости от того, было что-то удалено или нет
            throw new EntityNotFoundException("Лайк не найден!");
        }
    }

    public long getLikesCount() {
        return likes.size();
    }
}
