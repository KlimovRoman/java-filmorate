package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Primary
@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public User addUser(User userToAdd) {
        String sqlQuery = "insert into users(name, login, email, birthday) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, userToAdd.getName());
            stmt.setString(2, userToAdd.getLogin());
            stmt.setString(3, userToAdd.getEmail());
            stmt.setDate(4, Date.valueOf(userToAdd.getBirthday()));
            return stmt;
        }, keyHolder);
        int newFilmID =  keyHolder.getKey().intValue();
        //используем метод getUserById, чтобы убедиться что объект корректно добавлен в бд
        return  getUserById(newFilmID).orElseThrow(() -> new EntityNotFoundException("Юзер не найден в базе (был некорректно добавлен)"));
    }

    @Override
    public Optional<User> getUserById(int id) {
        // полученный юзер пока не обогащен друзьями
        String sql = "select * from users where id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        if(userRows.next()) {
            User user = new User();
            user.setId(userRows.getInt("id"));
            user.setName(userRows.getString("name"));
            user.setLogin(userRows.getString("login"));
            user.setEmail(userRows.getString("email"));
            user.setBirthday(userRows.getDate("birthday").toLocalDate());
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public User updUser(User userToUpd) {
        getUserById(userToUpd.getId()).orElseThrow(() -> new EntityNotFoundException("Юзер, который необходимо обновить не найден в базе"));
        String sqlQuery = "update users set " +
                "name = ?, login = ?, email = ?, birthday = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery
                , userToUpd.getName()
                , userToUpd.getLogin()
                , userToUpd.getEmail()
                , Date.valueOf(userToUpd.getBirthday())
                , userToUpd.getId());

        return getUserById(userToUpd.getId()).orElseThrow(() -> new EntityNotFoundException("Юзер не найден в базе"));
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "select * from users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    private User makeUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setLogin(rs.getString("login"));
        user.setEmail(rs.getString("email"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String sqlQuery = "insert into friendship(user_id, friend_id) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void delFriend(int userId, int friendId) {
        String sqlQuery = "delete from friendship where user_id = " + userId + " and friend_id = " + friendId;
        int count =  jdbcTemplate.update(sqlQuery);
        if(count == 0) {
            throw new  EntityNotFoundException("Юзер не найден в базе");
        }
    }

    @Override
    public List<User> getUserFriends(int id) {
        getUserById(id).orElseThrow(() -> new EntityNotFoundException("Юзер, который необходимо обновить не найден в базе"));
        String sql = "select * from friendship f left join users u on f.friend_id = u.id where user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs),id);
    }
}
