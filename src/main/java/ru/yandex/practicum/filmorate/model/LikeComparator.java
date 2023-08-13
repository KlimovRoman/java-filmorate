package ru.yandex.practicum.filmorate.model;

import java.util.Comparator;

public class LikeComparator implements Comparator<Film>  {
@Override
public int compare(Film o1, Film o2) {
    if(o1.getLikesCount() < o2.getLikesCount()) {
        return 1;
    } else if(o1.getLikesCount() > o2.getLikesCount()) {
        return -1;
    } else {
        return 0;
    }
}
}