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

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Slf4j
@Primary
@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
        int newFilmID = keyHolder.getKey().intValue();
        userToAdd.setId(newFilmID);
        return userToAdd;
    }

    @Override
    public Optional<User> getUserById(int id) {
        // полученный юзер пока не обогащен друзьями
        String sql = "select * from users where id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        return userMapper(userRows);
    }

    @Override
    public User updUser(User userToUpd) {
        String sqlQuery = "update users set " +
                "name = ?, login = ?, email = ?, birthday = ? " +
                "where id = ?";
        int rowUpdCnt = jdbcTemplate.update(sqlQuery,
                userToUpd.getName(),
                userToUpd.getLogin(),
                userToUpd.getEmail(),
                Date.valueOf(userToUpd.getBirthday()),
                userToUpd.getId());
        return userToUpd;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "select * from users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
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
        int count = jdbcTemplate.update(sqlQuery);
    }

    @Override
    public List<User> getUserFriends(int id) {
        String sql = "select * from friendship f left join users u on f.friend_id = u.id where user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
    }


    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        String sql = "select u.id, u.name, u.login, u.email, u.birthday \n" +
                "from friendship f left join users u on f.friend_id = u.id\n" +
                "where user_id = " + id + "\n" +
                "INTERSECT\n" +
                "select u.id, u.name, u.login, u.email, u.birthday \n" +
                "from friendship f left join users u on f.friend_id = u.id\n" +
                "where user_id = " + otherId;
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public void delUserById(int userId) {
        String sqlQuery = "DELETE FROM users  WHERE ID = ?";
        int count = jdbcTemplate.update(sqlQuery, userId);
    }

    @Override
    public boolean checkIdInDatabase(int id) {
        /*
        1. ревьюер рекомендовал подобные методы сделать boolean
        2. в прошлых реализациях (ensureСущностьExists() / сущностьGetById()) помимо проверки, что сущность есть,
        происходила ее десериализация, но в вызванных методах -- это лишние операции, по этой причине стоит только
        проверить ее существование в базе. по этой причине некоторые методы в ReviewService были
        удалены, так как после обновления перестали использоваться
        */

        String sql = "select * from users where id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, id);
        if (!sqlRowSet.next()) {
            String message = String.format("Пользователь с id: " + id + " не найден");
            log.error(message);
            throw new EntityNotFoundException(message);
        }
        return true;
    }

    private User commonFriendsMapper(ResultSet rs) throws SQLException {
        int friendId = rs.getInt("FRIEND_ID");
        return getUserById(friendId).get();
    }

    private Optional<User> userMapper(SqlRowSet userRows) {
        if (userRows.next()) {
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

    private User makeUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setLogin(rs.getString("login"));
        user.setEmail(rs.getString("email"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }
}