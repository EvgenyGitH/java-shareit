package ru.practicum.shareit.exception;

public class PaginationParamException extends RuntimeException {
    public PaginationParamException(String message) {
        super(message);
    }
}
