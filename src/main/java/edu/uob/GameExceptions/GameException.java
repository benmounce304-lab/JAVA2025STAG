package edu.uob.GameExceptions;

import java.io.Serial;

/**
 * Base class for all game-related exceptions.
 */

public class GameException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public GameException(String message) {
        super(message);
    }
}