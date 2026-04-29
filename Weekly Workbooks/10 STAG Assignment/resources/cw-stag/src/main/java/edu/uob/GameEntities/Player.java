package edu.uob.GameEntities;

import java.util.ArrayList;

public class Player extends GameEntity {

    private static final int MAX_HEALTH = 3;
    private Location currentLocation;
    private final ArrayList<Artefact> inventory;
    private int health;

    public Player(String name, String description) {
        super(name, description);

        this.currentLocation = null;
        this.inventory = new ArrayList<>();
        this.health = MAX_HEALTH;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int newHealth) {
        this.health = Math.min(newHealth, MAX_HEALTH);
    }

    public ArrayList<Artefact> getInventory() {
        return inventory;
    }

    public void addToInventory(Artefact artefact) {
        if (artefact == null) {
            throw new IllegalArgumentException("Artefact cannot be null");
        }
        this.inventory.add(artefact);
    }

    public void removeFromInventory(Artefact artefact) {
        if (artefact == null) {
            throw new IllegalArgumentException("Artefact cannot be null");
        }
        this.inventory.remove(artefact);
    }

    public boolean hasItem(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Item name cannot be null");
        }
        for (Artefact artefact : inventory) {
            if (artefact.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void damage(int amount) {
        this.health -= amount;
        if (this.health < 0) {
            this.health = 0;
        }
    }

    public void heal(int amount) {
        this.health += amount;
        if (this.health > MAX_HEALTH) {
            this.health = MAX_HEALTH;
        }
    }
}
