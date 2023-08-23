package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
private final UserStorage userStorage; //поле куда будет передано хранилище через контструктор с помощью зависимостей

    //связали зависимостью  сервис и хранилище
    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }



    public void addFriend(int id, int friendId) {
        userStorage.addFriend(id, friendId);
    }


    public void delFriend(int id, int friendId) {
        userStorage.delFriend(id, friendId);
    }



    public List<User> getUserFriends(int id) {
       return userStorage.getUserFriends(id);
    }

    public List<User> getCommonFriendsDEL(int id, int otherId) {
        User usr1 = getUserById(id);
        User usr2 = getUserById(otherId);
        if (usr1 != null && usr2 != null) {
            Set<Integer> frSet1 = usr1.getFriends();
            Set<Integer> frSet2 = usr2.getFriends();
            Set<Integer> commonFriendsIds = frSet1.stream().filter(frSet2::contains).collect(Collectors.toSet()); //нашли пересечение
            return commonFriendsIds.stream().map(this::getUserById).collect(Collectors.toList()); //дсотали пользоваетелей и упокавали в список
        } else {
            throw new EntityNotFoundException("Пользователи или пользователь не найдены");
        }
    }

    public List<User> getCommonFriends(int id, int otherId) {
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
        return userStorage.getUserById(id).orElseThrow(() -> new  EntityNotFoundException("пользователь не найден!"));
    }

    private void nameValidationAndSetName(User usr) {
        if (usr.getName() == null || usr.getName().isBlank()) {
            usr.setName(usr.getLogin());
        }
    }
}
