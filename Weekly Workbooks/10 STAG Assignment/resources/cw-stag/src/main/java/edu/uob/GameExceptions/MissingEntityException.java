package edu.uob.GameExceptions;

public class MissingEntityException extends GameException {
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