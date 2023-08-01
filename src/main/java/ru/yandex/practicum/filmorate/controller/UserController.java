package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;

import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private HashMap<Integer, User> users = new HashMap<>();
    private int userIdCounter = 0;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @PostMapping
    public User addUser(@Valid @RequestBody User userToAdd) {
        userIdCounter++;
        nameValidationAndSetName(userToAdd);
        userToAdd.setId(userIdCounter);
        users.put(userIdCounter, userToAdd);
        log.info("Добавлен юзер с id = {}", userIdCounter);
        return userToAdd;
    }

    @PutMapping
    public User updUser(@Valid @RequestBody User userToUpd) {

        final int id = userToUpd.getId();
        User user = users.get(id);
        if (user != null) {
            user.setBirthday(userToUpd.getBirthday());
            user.setName(userToUpd.getName());
            user.setLogin(userToUpd.getLogin());
            user.setEmail(userToUpd.getEmail());
            nameValidationAndSetName(userToUpd);
            log.info("Обновлен юзер с id = {}", id);
            return user;
        } else {
            log.info("пользователь не найден!");
            throw new EntityNotFoundException("пользователь не найден!");
        }

    }

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private void nameValidationAndSetName(User usr) {
        if (usr.getName() == null || usr.getName().isBlank()) {
            usr.setName(usr.getLogin());
        }
    }
}
