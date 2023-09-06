package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;


public interface UserStorage {
    public User addUser(User userToAdd);

    public Optional<User> getUserById(int id);

    public User updUser(User userToUpd);

    public List<User> getAllUsers();

    public void addFriend(int userId, int friendId);


    public void delFriend(int id, int friendId);

    public List<User> getUserFriends(int id);

    public List<User> getCommonFriends(int id, int otherId);

    public void delUserById(int userId);
}
