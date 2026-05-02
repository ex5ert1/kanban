package ru.kanban.exception;

public class InvalidEpicForSubtaskException extends RuntimeException {
    public InvalidEpicForSubtaskException(String message) {
        super(message);
    }

    public InvalidEpicForSubtaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
