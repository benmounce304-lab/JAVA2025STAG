package edu.uob.GameExceptions;

import java.io.Serial;

/**
 * Custom exception for game-related errors.
 */

public class GameException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public GameException(String message) {
        super(message);
    }
}