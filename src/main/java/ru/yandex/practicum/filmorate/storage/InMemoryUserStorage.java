package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private HashMap<Integer, User> users = new HashMap<>();
    private int userIdCounter = 0;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public User addUser(User userToAdd) {
        userIdCounter++;
        nameValidationAndSetName(userToAdd);
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
            nameValidationAndSetName(userToUpd);
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
    public User getUserById(int id) {
        return users.get(id);
    }

    private void nameValidationAndSetName(User usr) {
        if (usr.getName() == null || usr.getName().isBlank()) {
            usr.setName(usr.getLogin());
        }
    }
}
