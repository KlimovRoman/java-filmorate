package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.constant.EventType;
import ru.yandex.practicum.filmorate.constant.OperationType;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage; //поле куда будет передано хранилище через контструктор с помощью зависимостей
    private final EventStorage eventStorage;

    //связали зависимостью  сервис и хранилище
    @Autowired
    public UserService(UserStorage userStorage, EventStorage eventStorage) {
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
    }

    public void addFriend(int id, int friendId) {
        userStorage.checkIdInDatabase(id);
        userStorage.checkIdInDatabase(friendId);

        userStorage.addFriend(id, friendId);
        eventStorage.addEvent(id, friendId, OperationType.ADD, EventType.FRIEND);
    }

    public void delFriend(int id, int friendId) {
        userStorage.checkIdInDatabase(id);
        userStorage.checkIdInDatabase(friendId);

        userStorage.delFriend(id, friendId);
        eventStorage.addEvent(id, friendId, OperationType.REMOVE, EventType.FRIEND);
    }

    public void delUserById(int userId) {
        userStorage.checkIdInDatabase(userId);

        userStorage.delUserById(userId);
    }

    public List<User> getUserFriends(int id) {
        userStorage.checkIdInDatabase(id);

        return userStorage.getUserFriends(id);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        userStorage.checkIdInDatabase(id);
        userStorage.checkIdInDatabase(otherId);

        return userStorage.getCommonFriends(id, otherId);
    }

    public User addUser(User userToAdd) {
        nameValidationAndSetName(userToAdd);
        return userStorage.addUser(userToAdd);
    }

    public User updUser(User userToUpd) {
        userStorage.checkIdInDatabase(userToUpd.getId());

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
        userStorage.checkIdInDatabase(id);

        return eventStorage.getFeedByUserId(id);
    }

    private void nameValidationAndSetName(User usr) {
        if (usr.getName() == null || usr.getName().isBlank()) {
            usr.setName(usr.getLogin());
        }
    }
}
