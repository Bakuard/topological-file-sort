package com.bakuard.helloJGraphT;

/**
 * Указывает на попытку использовать топологическую сортировку для циклического ориентированного графа.
 * В сообщении к ошибке будет указан найденный цикл.
 */
public class CycleGraphException extends RuntimeException {

    public CycleGraphException() {
    }

    public CycleGraphException(String message) {
        super(message);
    }

    public CycleGraphException(String message, Throwable cause) {
        super(message, cause);
    }

    public CycleGraphException(Throwable cause) {
        super(cause);
    }

    public CycleGraphException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
