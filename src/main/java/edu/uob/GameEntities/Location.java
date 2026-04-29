package edu.uob.GameEntities;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a location in the game world, which can contain artefacts, furniture, and characters.
 */

public class Location extends GameEntity {

    private final HashMap<String, Location> paths;
    private final ArrayList<Artefact> artefacts;
    private final ArrayList<Furniture> furniture;
    private final ArrayList<Character> characters;

    public Location(String name, String description) {
        super(name, description);
        this.paths = new HashMap<>();
        this.artefacts = new ArrayList<>();
        this.furniture = new ArrayList<>();
        this.characters = new ArrayList<>();
    }

    public ArrayList<Character> getCharacters() {
        return new ArrayList<>(this.characters);
    }

    public ArrayList<Furniture> getFurniture() {
        return new ArrayList<>(this.furniture);
    }

    public ArrayList<Artefact> getArtefacts() {
        return new ArrayList<>(this.artefacts);
    }

    public HashMap<String, Location> getPaths() {
        return new HashMap<>(this.paths);
    }

    public void addArtefact(Artefact artefact) {
        if (artefact == null) throw new IllegalArgumentException("Artefact cannot be null");
        this.artefacts.add(artefact);
    }

    public void removeArtefact(Artefact artefact) {
        if (artefact == null) throw new IllegalArgumentException("Artefact cannot be null");
        this.artefacts.remove(artefact);
    }

    public void addFurniture(Furniture furniture) {
        if (furniture == null) throw new IllegalArgumentException("Furniture cannot be null");
        this.furniture.add(furniture);
    }

    public void removeFurniture(Furniture furniture) {
        if (furniture == null) throw new IllegalArgumentException("Furniture cannot be null");
        this.furniture.remove(furniture);
    }

    public void addCharacter(Character character) {
        if (character == null) throw new IllegalArgumentException("Character cannot be null");
        this.characters.add(character);
    }

    public void removeCharacter(Character character) {
        if (character == null) throw new IllegalArgumentException("Character cannot be null");
        this.characters.remove(character);
    }

    public Artefact findArtefactByName(String name) {
        if (name == null) throw new IllegalArgumentException("Artefact name cannot be null");
        return artefacts.stream()
                .filter(artefact -> artefact.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public Character findCharacterByName(String name) {
        if (name == null) throw new IllegalArgumentException("Character name cannot be null");
        return characters.stream()
                .filter(character -> character.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public Furniture findFurnitureByName(String name) {
        if (name == null) throw new IllegalArgumentException("Furniture name cannot be null");
        return furniture.stream()
                .filter(furniture -> furniture.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public void addPath(String direction, Location location) {
        if (direction == null || location == null)
            throw new IllegalArgumentException("Direction and location cannot be null");
        this.paths.put(direction, location);
    }

}