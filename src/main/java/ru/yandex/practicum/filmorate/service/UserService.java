package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class UserService {
private final UserStorage userStorage; //поле куда будет передано хранилище через контструктор с помощью зависимостей

    //связали зависимостью  сервис и хранилище
    @Autowired
    public UserService (UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User userToAdd) {
        return userStorage.addUser(userToAdd);
    }

    public User updUser(User userToUpd) {
        return userStorage.updUser(userToUpd);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

}
