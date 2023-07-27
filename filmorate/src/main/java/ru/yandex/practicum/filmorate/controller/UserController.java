package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
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
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @PostMapping
    public void addUser(@Valid @RequestBody User userToAdd) {
        userIdCounter++;
        User newUser = new User(userIdCounter);
        if (!nameValidation(userToAdd)) {
            newUser.setName(userToAdd.getLogin());
        } else {
            newUser.setName(userToAdd.getName());
        }
        newUser.setLogin(userToAdd.getLogin());
        newUser.setEmail(userToAdd.getEmail());
        newUser.setBirthday(userToAdd.getBirthday());
        users.put(userIdCounter, newUser);
        log.info("Добавлен юзер с id = {}", userIdCounter);
    }

    @PutMapping
    public void updUser(@Valid @RequestBody User userToUpd) {

        final int id = userToUpd.getId();
        if (users.containsKey(id)) {
            User userFromHash = users.get(id);
            userFromHash.setBirthday(userToUpd.getBirthday());
            if (!nameValidation(userToUpd)) {
                userFromHash.setName(userToUpd.getLogin());
            } else {
                userFromHash.setName(userToUpd.getName());
            }
            userFromHash.setLogin(userToUpd.getLogin());
            userFromHash.setEmail(userToUpd.getEmail());
            log.info("Обновлен юзер с id = {}", id);
        } else {
            log.info("пользователь не найден!");
            throw new EntityNotFoundException("пользователь не найден!");
        }
    }

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private boolean nameValidation(User usr) {
        if (usr.getName() == null || usr.getName().isBlank()) {
            return false;
        } else {
            return true;
        }
    }
}
