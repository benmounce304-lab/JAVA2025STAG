package edu.uob.GameExceptions;

import java.io.Serial;

/**
 * Exception thrown when a player's action is ambiguous, such as when multiple commands match the input.
 */

public class AmbiguousActionException extends GameException {
    @Serial
    private static final long serialVersionUID = 1L;

    public AmbiguousActionException(String message) {
        super(message);
    }
}
