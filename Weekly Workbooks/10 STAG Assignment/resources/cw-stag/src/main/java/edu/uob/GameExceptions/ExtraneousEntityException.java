package edu.uob.GameExceptions;

public class ExtraneousEntityException extends GameException {
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