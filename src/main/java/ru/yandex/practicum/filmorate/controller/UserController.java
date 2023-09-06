package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService; //поле куда будет передан сервис через контструктор с помощью зависимостей

    //связали зависимостью контроллер и сервис через аннотацию RequiredArgsConstructor

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

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
       return userService.getUserById(id);
    }

    @DeleteMapping("/{userId}")
    public void delUserById(@PathVariable int userId) {
        userService.delUserById(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void delFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.delFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable int id) {
       return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getFeedById(@PathVariable int id) {
        return userService.getFeedById(id);
    }
}
