package ru.kanban.exception;

public class InvalidEpicForSubtaskException extends TaskManagerException {
    public InvalidEpicForSubtaskException(String message) {
        super(message);
    }

    public InvalidEpicForSubtaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
