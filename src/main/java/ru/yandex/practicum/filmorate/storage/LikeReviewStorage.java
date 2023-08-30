package ru.yandex.practicum.filmorate.storage;

public interface LikeReviewStorage {

    void createLike(int id, int userId);

    void createDislike(int id, int userId);

    void deleteLike(int id, int userId);

    void deleteDislike(int id, int userId);

}
