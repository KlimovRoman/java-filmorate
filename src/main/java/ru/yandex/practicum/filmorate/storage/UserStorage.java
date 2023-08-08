package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;


public interface UserStorage {
    public User addUser(User userToAdd);

    public User updUser(User userToUpd);

    public List<User> getAllUsers();

    //public void delUser();
}
