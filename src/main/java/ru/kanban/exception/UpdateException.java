package ru.kanban.exception;

public class UpdateException extends TaskManagerException {
    public UpdateException(String message) {
        super(message);
    }

    public UpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
