package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;


public interface UserStorage {
    public User addUser(User userToAdd);

    public User getUserById(int id);

    public User updUser(User userToUpd);

    public List<User> getAllUsers();
}
