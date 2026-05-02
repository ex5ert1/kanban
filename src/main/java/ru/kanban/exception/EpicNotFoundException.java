package ru.kanban.exception;

public class EpicNotFoundException extends RuntimeException {
    public EpicNotFoundException(String message) {
        super(message);
    }

    public EpicNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
