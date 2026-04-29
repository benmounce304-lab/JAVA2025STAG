package edu.uob.GameExceptions;

public class AmbiguousActionException extends GameException {
    private static final long serialVersionUID = 1L;

    public AmbiguousActionException(String message) {
        super(message);
    }
}
