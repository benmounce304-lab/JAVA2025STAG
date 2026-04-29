package edu.uob.GameExceptions;

import java.io.Serial;

/**
 * Exception thrown when a required entity is missing in the game state.
 */

public class MissingEntityException extends GameException {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String entityName;

    public MissingEntityException(String entityName) {
        super("Missing required entity: " + entityName);
        this.entityName = entityName;
    }

    public String getEntityName() {
        return entityName;
    }
}