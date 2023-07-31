package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    private int id;
    @NotEmpty
    @Email
    private String email;

    @NotBlank @Pattern(regexp = "\\S+")
    private String login;

    private String name;
    @NotNull @PastOrPresent
    private LocalDate birthday;

    public User() {
    }

    public User(int id) {
        this.id = id;
    }
}