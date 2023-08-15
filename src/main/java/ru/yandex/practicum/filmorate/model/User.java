package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private int id;
    @NotEmpty
    @Email
    private String email;
    @JsonIgnore
    private Set<Integer> friends = new HashSet<>();

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

    public void addFriend(int idFriend) {
        friends.add(idFriend);
    }

    public void delFriend(int idFriend) {
        if (friends.contains(idFriend)) {
            friends.remove(idFriend);
        }
    }
}