package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class User {
    private int id;
    @Email
    private String email;

    @NotNull
    @NotBlank
    private String login;

    private String name;
    private String birthday;

    public User(){

    }
    public User(int id){
        this.id = id;
    }
}
