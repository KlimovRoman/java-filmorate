package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService; //поле куда будет передан сервис через контструктор с помощью зависимостей

    //связали зависимостью контроллер и сервис
    @Autowired
    public UserController (UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User userToAdd) {
        return userService.addUser(userToAdd);
    }

    @PutMapping
    public User updUser(@Valid @RequestBody User userToUpd) {
        return userService.updUser(userToUpd);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

}
