package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private HashMap<Integer, User> users = new HashMap<>();
    private int userIdCounter = 0;

    @Override
    public User addUser(User userToAdd) {
        userIdCounter++;
        userToAdd.setId(userIdCounter);
        users.put(userIdCounter, userToAdd);
        log.info("Добавлен юзер с id = {}", userIdCounter);
        return userToAdd;
    }


    @Override
    public User updUser(User userToUpd) {
        final int id = userToUpd.getId();
        User user = users.get(id);
        if (user != null) {
            user.setBirthday(userToUpd.getBirthday());
            user.setName(userToUpd.getName());
            user.setLogin(userToUpd.getLogin());
            user.setEmail(userToUpd.getEmail());
            log.info("Обновлен юзер с id = {}", id);
            return user;
        } else {
            log.info("пользователь не найден!");
            throw new EntityNotFoundException("пользователь не найден!");
        }
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUserById(int id) {
        User user = users.get(id);
        if (user == null) {
            throw  new EntityNotFoundException("пользователь не найден!");
        } else {
            return Optional.ofNullable(users.get(id));
        }
    }

}
