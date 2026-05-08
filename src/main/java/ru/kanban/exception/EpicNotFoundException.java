package ru.kanban.exception;

public class EpicNotFoundException extends NotFoundException {
    public EpicNotFoundException(String message) {
        super(message);
    }

    public EpicNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
