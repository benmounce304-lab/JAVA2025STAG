package edu.uob.GameExceptions;

public class LoadFileException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public LoadFileException(String message, Throwable cause) {
        super(message, cause);
    }
}