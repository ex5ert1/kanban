package ru.kanban.exception;

public class SubtaskNotFoundException extends RuntimeException {
    public SubtaskNotFoundException(String message) {
        super(message);
    }

    public SubtaskNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
