package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;

public class Location extends GameEntity {

    private HashMap<String, Location> paths;
    private ArrayList<Artefact> artefacts;
    private ArrayList<Furniture> furniture;
    private ArrayList<Character> characters;

    public Location(String name, String description) {
        super(name, description);

        this.paths = new HashMap<>();
        this.artefacts = new ArrayList<>();
        this.furniture = new ArrayList<>();
        this.characters = new ArrayList<>();

    }

    public ArrayList<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(ArrayList<Character> characters) {
        this.characters = characters;
    }

    public ArrayList<Furniture> getFurniture() {
        return furniture;
    }

    public void setFurniture(ArrayList<Furniture> furniture) {
        this.furniture = furniture;
    }

    public ArrayList<Artefact> getArtefacts() {
        return artefacts;
    }

    public void setArtefacts(ArrayList<Artefact> artefacts) {
        this.artefacts = artefacts;
    }

    public HashMap<String, Location> getPaths() {
        return paths;
    }

    public void setPaths(HashMap<String, Location> paths) {
        this.paths = paths;
    }

    public void addArtefact(Artefact artefact) {
        if (artefact == null) {
            throw new IllegalArgumentException("Artefact cannot be null");
        }
        this.artefacts.add(artefact);
    }

    public void removeArtefact(Artefact artefact) {
        if (artefact == null) {
            throw new IllegalArgumentException("Artefact cannot be null");
        }
        this.artefacts.remove(artefact);
    }

    public Artefact findArtefactByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Artefact name cannot be null");
        }
        return artefacts.stream()
                .filter(artefact -> artefact.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
    public void addPath(String direction, Location location) {
        if (direction == null || location == null) {
            throw new IllegalArgumentException("Direction and location cannot be null");
        }
        this.paths.put(direction, location);
    }

    public void moveArtefactTo(Artefact artefact, Location destination) {
        if (artefact == null || destination == null) {
            throw new IllegalArgumentException("Artefact and destination cannot be null");
        }
        if (this.artefacts.contains(artefact)) {
            this.artefacts.remove(artefact);
            destination.addArtefact(artefact);
        }
    }
}