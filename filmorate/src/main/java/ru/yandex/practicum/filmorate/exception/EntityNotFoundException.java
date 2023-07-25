package ru.yandex.practicum.filmorate.exception;
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException() {}
        
    public EntityNotFoundException(String msg) {
        super(msg);
    }
}
