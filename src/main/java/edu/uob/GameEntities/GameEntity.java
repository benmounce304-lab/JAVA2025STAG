package edu.uob.GameEntities;

/**
 * Represents a generic entity in the game world, which can be interacted with by players.
 */

public abstract class GameEntity {
    private final String name;
    private final String description;

    public GameEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
