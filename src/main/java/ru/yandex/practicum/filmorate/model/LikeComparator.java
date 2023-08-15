package ru.yandex.practicum.filmorate.model;

import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class LikeComparator implements Comparator<Film> {
    @Override
    public int compare(Film o1, Film o2) {
        return Long.compare(o2.getLikesCount(), o1.getLikesCount());
    }
}