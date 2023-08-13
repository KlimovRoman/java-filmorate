package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
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
        User usr1 = userStorage.getUserById(id);
        User usr2 = userStorage.getUserById(friendId);
        if(usr1 != null && usr2 != null) {
            usr1.addFriend(friendId);
            usr2.addFriend(id);
        }
    }

    public void delFriend(int id, int friendId) {
        User usr1 = userStorage.getUserById(id);
        User usr2 = userStorage.getUserById(friendId);
        if(usr1 != null && usr2 != null) {
            usr1.delFriend(friendId);
            usr2.delFriend(id);
        }
    }

    public List<User> getUserFriends(int id) {
        User usr = userStorage.getUserById(id);
        if(usr != null) {
            Set<Integer> frSet =  usr.getFriends();
            List<User> frList = new ArrayList<>();
            for(Integer frId: frSet){
                frList.add(userStorage.getUserById(frId));
            }
            return frList;
        } else {
            return null;
        }
    }

    public List<User> getCommonFriends(int id, int otherId) {
        List<User> commonFriends = new ArrayList<>();
        User usr1 = userStorage.getUserById(id);
        User usr2 = userStorage.getUserById(otherId);
        if(usr1 != null && usr2 != null) {
            Set<Integer> frSet1 = usr1.getFriends();
            Set<Integer> frSet2 = usr2.getFriends();
            Set<Integer> commonFriendsIds = frSet1.stream().filter(frSet2::contains).collect(Collectors.toSet()); //нашли пересечение
            for(int comId: commonFriendsIds) {
                commonFriends.add(userStorage.getUserById(comId));
            }
            return commonFriends;
        } else {
            return null;
        }
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

    public User getUserById(int id) {

        return userStorage.getUserById(id);
    }

}
