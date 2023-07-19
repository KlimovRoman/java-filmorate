package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public void addUser (@Valid @RequestBody User userToAdd){
        if(emailValid(userToAdd) && loginValid(userToAdd) &&
                        birthValid(userToAdd)) {
            userIdCounter++;
            User newUser = new User(userIdCounter);
            if(userToAdd.getName() == null || userToAdd.getName().equals("null") || userToAdd.getName().isEmpty() ||
                                                      userToAdd.getName().isBlank()){
                newUser.setName(userToAdd.getLogin());
            } else {
                newUser.setName(userToAdd.getName());
            }
            newUser.setLogin(userToAdd.getLogin());
            newUser.setEmail(userToAdd.getEmail());
            newUser.setBirthday(userToAdd.getBirthday());
            users.put(userIdCounter, newUser);
            log.info("Добавлен юзер с id=" + userIdCounter);
        } else {
            log.error("Ошибка валидации юзер не создан");
            throw new ValidationException("Не пройден один валидаторов");
        }

    }

    @PutMapping
    public void updUser (@Valid @RequestBody User userToUpd){

        if(emailValid(userToUpd) && loginValid(userToUpd) &&
                                birthValid(userToUpd)) {

            final int id = userToUpd.getId();
            if(users.containsKey(id)){
                User userFromHash = users.get(id);
                userFromHash.setBirthday(userToUpd.getBirthday());
                if(userToUpd.getName() == null || userToUpd.getName().equals("null") ||
                        userToUpd.getName().isEmpty() || userToUpd.getName().isBlank()){
                    userFromHash.setName(userToUpd.getLogin());
                } else {
                    userFromHash.setName(userToUpd.getName());
                }
                userFromHash.setLogin(userToUpd.getLogin());
                userFromHash.setEmail(userToUpd.getEmail());
                log.info("Обновлен юзер с id=" + id);
            } else {
                log.error("пользователь не найден!");
                throw new UserNotFoundException("пользователь не найден!");
            }
        } else {
            log.error("Ошибка валидации юзер не обновлен");
            throw new ValidationException("Не пройден один валидаторов");
        }
    }

    @GetMapping
    public List<User> getAllUsers (){
    return new ArrayList<>(users.values());
    }


    // методы для валидации

    private boolean emailValid(User user){
        if (user.getEmail()!=null && !user.getEmail().isBlank() &&
                !user.getEmail().isEmpty() && user.getEmail().contains("@")){
            return true;
        }else {
            log.error("Валидация не пройдена, email неверный");
            return false;
        }

    }


    private boolean loginValid (User user){
        if (user.getLogin()!=null && !user.getLogin().isBlank() &&
                !user.getLogin().isEmpty() && !user.getLogin().contains(" ")){
            return true;
        }else {
            log.error("Валидация не пройдена, login пустой");
            return false;
        }
    }

    private boolean birthValid (User user) {
        LocalDate dateToCheck = LocalDate.parse(user.getBirthday(),formatter);
        LocalDate dateForCompare =  LocalDate.now();
        if(dateToCheck.isBefore(dateForCompare)){
            return true;
        } else {
            log.error("Валидация не пройдена, неверная дата рождения");
            return false;
        }
    }

}
