package edu.uob.GameEngine;

import edu.uob.GameEntities.Location;
import edu.uob.GameEntities.Player;
import edu.uob.GameExceptions.LoadFileException;

import java.io.File;
import java.util.*;

/**
 * Represents the core game model containing all game states and entities.
 */
public class GameModel {
    private final HashMap<String, Location> allLocations;
    private final ArrayList<GameAction> allActions;
    private final HashMap<String, Player> allPlayers;
    private final Location startingRoom;

    public GameModel(File entitiesFile, File actionsFile) {
        try {
            EntityParser entityParser = new EntityParser();
            this.allLocations = entityParser.parseEntities(entitiesFile);
            this.startingRoom = entityParser.getStartingRoom();

            if (this.startingRoom == null) {
                throw new IllegalArgumentException("Starting room not found in entities file");
            }

            ActionParser actionParser = new ActionParser();
            this.allActions = actionParser.parseActions(actionsFile);

            this.allPlayers = new HashMap<>();
        } catch (Exception e) {
            throw new LoadFileException("Error loading game files: " + e.getMessage(), e);
        }
    }

    public Location getStartingRoom() {
        return startingRoom;
    }

    public Map<String, Location> getAllLocations() {
        return Collections.unmodifiableMap(allLocations);
    }

    public List<GameAction> getAllActions() {
        return new ArrayList<>(allActions);
    }

    public Map<String, Player> getAllPlayers() {
        return Collections.unmodifiableMap(allPlayers);
    }

    public void addPlayer(String playerName, Player player) {
        if (playerName == null || player == null) {
            throw new IllegalArgumentException("Player name and player cannot be null");
        }
        allPlayers.put(playerName, player);
    }
}