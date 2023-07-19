package ru.yandex.practicum.filmorate.exception;

public class FilmNotFoundException extends RuntimeException{
    public FilmNotFoundException (){

    }
    public FilmNotFoundException (String msg) {
        super(msg);
    }
}
