package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;

public class Player extends GameEntity {

    private Location currentLocation;
    private ArrayList<Artefact> inventory;
    private int health;

    public Player(String name, String description) {
        super(name, description);

        this.currentLocation = null;
        this.inventory = new ArrayList<>();
        this.health = 3;
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
        if (newHealth > 3) {
            this.health = 3;
        } else {
            this.health = newHealth;
        }
    }

    public ArrayList<Artefact> getInventory() {
        return inventory;
    }

    public void setInventory(ArrayList<Artefact> inventory) {
        this.inventory = inventory;
    }

    public void moveTo(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        this.currentLocation = location;
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
        if (amount < 0) {
            throw new IllegalArgumentException("Damage amount cannot be negative");
        }
        this.health -= amount;
        if (this.health < 0) {
            this.health = 0;
        }
    }

    public void heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Healing amount cannot be negative");
        }
        this.health += amount;
        if (this.health > 100) {
            this.health = 100;
        }
    }
}
