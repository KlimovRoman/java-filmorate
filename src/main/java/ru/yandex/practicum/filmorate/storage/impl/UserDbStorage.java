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
        int newFilmID =  keyHolder.getKey().intValue();
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
        if (rowUpdCnt < 1) {
            throw new EntityNotFoundException("Юзер, который необходимо обновить не найден в базе");
        }
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
        int count =  jdbcTemplate.update(sqlQuery);
        if (count == 0) {
            throw new  EntityNotFoundException("Юзер не найден в базе(удаление не прошло)");
        }
    }

    @Override
    public List<User> getUserFriends(int id) {
        String sql = "select * from friendship f left join users u on f.friend_id = u.id where user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs),id);
    }


    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        List<User> listForReturn = new ArrayList<>();
        String sql = "select f.FRIEND_ID  \n" +
                "from friendship f left join users u on f.friend_id = u.id\n" +
                "where user_id = " + id + "\n" +
                "INTERSECT\n" +
                "select f.FRIEND_ID  \n" +
                "from friendship f left join users u on f.friend_id = u.id\n" +
                "where user_id = " + otherId;
        return  jdbcTemplate.query(sql, (rs, rowNum) -> commonFriendsMapper(rs));
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