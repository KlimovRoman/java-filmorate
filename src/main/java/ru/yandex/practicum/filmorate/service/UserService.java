package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage; //поле куда будет передано хранилище через контструктор с помощью зависимостей
    private final EventDbStorage eventDbStorage;

    //связали зависимостью  сервис и хранилище
    @Autowired
    public UserService(UserStorage userStorage, EventDbStorage eventDbStorage) {
        this.userStorage = userStorage;
        this.eventDbStorage = eventDbStorage;
    }

    public void addFriend(int id, int friendId) {
        userStorage.getUserById(id).orElseThrow(() -> new EntityNotFoundException("Пользователи или пользователь не найдены"));
        userStorage.getUserById(friendId).orElseThrow(() -> new EntityNotFoundException("Пользователи или пользователь не найдены"));
        userStorage.addFriend(id, friendId);
    }


    public void delFriend(int id, int friendId) {
        userStorage.getUserById(id).orElseThrow(() -> new EntityNotFoundException("Пользователи или пользователь не найдены"));
        userStorage.getUserById(friendId).orElseThrow(() -> new EntityNotFoundException("Пользователи или пользователь не найдены"));
        userStorage.delFriend(id, friendId);
    }

    public void delUserById(int userId) {
        userStorage.delUserById(userId);
    }

    public List<User> getUserFriends(int id) {
        userStorage.getUserById(id).orElseThrow(() -> new EntityNotFoundException("Юзер не найден в базе"));
        return userStorage.getUserFriends(id);
    }


    public List<User> getCommonFriends(int id, int otherId) {
        userStorage.getUserById(id).orElseThrow(() -> new EntityNotFoundException("Пользователи или пользователь не найдены"));
        userStorage.getUserById(otherId).orElseThrow(() -> new EntityNotFoundException("Пользователи или пользователь не найдены"));
        return userStorage.getCommonFriends(id, otherId);
    }

    public User addUser(User userToAdd) {
        nameValidationAndSetName(userToAdd);
        return userStorage.addUser(userToAdd);
    }

    public User updUser(User userToUpd) {
        nameValidationAndSetName(userToUpd);
        return userStorage.updUser(userToUpd);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id).orElseThrow(() -> new EntityNotFoundException("пользователь не найден!"));
    }

    public List<Event> getFeedByUserId(int id) {
        userStorage.getUserById(id).orElseThrow(() -> new EntityNotFoundException("пользователь не найден!"));
        return eventDbStorage.getFeedByUserId(id);
    }

    private void nameValidationAndSetName(User usr) {
        if (usr.getName() == null || usr.getName().isBlank()) {
            usr.setName(usr.getLogin());
        }
    }
}
