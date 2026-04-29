package edu.uob.GameExceptions;

import java.io.Serial;

/**
 * Exception thrown when there is an error loading game files, such as missing or corrupted files.
 */

public class LoadFileException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public LoadFileException(String message, Throwable cause) {
        super(message, cause);
    }
}