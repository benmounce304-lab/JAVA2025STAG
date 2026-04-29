package edu.uob.GameExceptions;

import java.io.Serial;

/**
 * Exception thrown when a command contains an entity that is not expected or allowed in that context.
 */

public class ExtraneousEntityException extends GameException {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String entityName;

    public ExtraneousEntityException(String entityName) {
        super("Extraneous entity in command: " + entityName);
        this.entityName = entityName;
    }

    public String getEntityName() {
        return entityName;
    }
}