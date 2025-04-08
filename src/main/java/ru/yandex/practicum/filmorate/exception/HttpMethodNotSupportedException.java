package ru.yandex.practicum.filmorate.exception;

public class HttpMethodNotSupportedException extends RuntimeException {
    public HttpMethodNotSupportedException(String message) {
        super(message);
    }
}