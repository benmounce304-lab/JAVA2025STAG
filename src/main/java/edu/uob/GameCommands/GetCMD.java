package edu.uob.GameCommands;

import edu.uob.GameEntities.Artefact;
import edu.uob.GameEntities.Furniture;
import edu.uob.GameEntities.Location;
import edu.uob.GameEngine.CommandResult;
import edu.uob.GameEngine.GameModel;
import edu.uob.GameEntities.Player;

/**
 * The GET command - allows players to pick up artefacts from the current location.
 * Artefacts are items that can be carried, while furniture is too heavy to pick up.
 * Supports both exact matching and fuzzy matching (e.g., "get razor sharp axe" finds "axe").
 */
public class GetCMD implements PlayerCMD {
    private static final String[] ARTICLES = {"the ", "a ", "an "};
    private final String rawCommand;

    /**
     * Creates a new GetCMD with the raw command string.
     * @param rawCommand The command string starting with "get "
     */
    public GetCMD(String rawCommand) {
        this.rawCommand = rawCommand;
    }

    /**
     * Executes the GET command to pick up an artefact from the current location.
     * First checks for ambiguity, then tries exact/fuzzy matching.
     * @param player The player executing the command
     * @param model The game model
     * @return CommandResult indicating success or failure
     */
    @Override
    public CommandResult execute(Player player, GameModel model) {
        Location currentRoom = player.getCurrentLocation();
        String lowerCmd = rawCommand.toLowerCase();

        int matchCount = 0;
        Artefact matchedArtefact = null;
        Furniture matchedFurniture = null;

        for (Artefact a : currentRoom.getArtefacts()) {
            if (lowerCmd.matches(".*\\b" + a.getName().toLowerCase() + "\\b.*")) {
                matchCount++;
                matchedArtefact = a;
            }
        }
        for (Furniture f : currentRoom.getFurniture()) {
            if (lowerCmd.matches(".*\\b" + f.getName().toLowerCase() + "\\b.*")) {
                matchCount++;
                matchedFurniture = f;
            }
        }

        if (matchCount > 1) {
            return CommandResult.failure("There is more than one thing you can 'get' here. Be more specific!");
        }

        // If they cleanly mentioned exactly one thing, execute it immediately!
        if (matchCount == 1) {
            if (matchedArtefact != null) {
                currentRoom.removeArtefact(matchedArtefact);
                player.addToInventory(matchedArtefact);
                return CommandResult.success("You picked up the " + matchedArtefact.getName() + ".");
            }
            return CommandResult.failure("You cannot pick up " + matchedFurniture.getName() + ", it's too heavy for you to carry.");
        }

        String targetItemName = extractAndNormalizeItemName(rawCommand.substring(4).trim());

        for (Artefact a : currentRoom.getArtefacts()) {
            if (a.getName().equalsIgnoreCase(targetItemName)) {
                currentRoom.removeArtefact(a);
                player.addToInventory(a);
                return CommandResult.success("You picked up the " + targetItemName + ".");
            }
        }

        Artefact fuzzyMatch = findFuzzyMatchArtefact(targetItemName, currentRoom);
        if (fuzzyMatch != null) {
            currentRoom.removeArtefact(fuzzyMatch);
            player.addToInventory(fuzzyMatch);
            return CommandResult.success("You picked up the " + fuzzyMatch.getName() + ".");
        }

        for (Furniture f : currentRoom.getFurniture()) {
            if (f.getName().equalsIgnoreCase(targetItemName)) {
                return CommandResult.failure("You cannot pick up " + targetItemName + ", it's too heavy for you to carry.");
            }
        }

        Furniture fuzzyFurniture = findFuzzyMatchFurniture(targetItemName, currentRoom);
        if (fuzzyFurniture != null) {
            return CommandResult.failure("You cannot pick up " + fuzzyFurniture.getName() + ", it's too heavy for you to carry.");
        }

        return CommandResult.failure("There is no " + targetItemName + " in this room.");
    }

    /**
     * Finds an artefact using fuzzy matching.
     */
    private Artefact findFuzzyMatchArtefact(String targetItemName, Location locationOrPlayer) {
        String[] keywords = targetItemName.toLowerCase().split("\\s+");

        for (Artefact a : locationOrPlayer.getArtefacts()) {
            java.util.List<String> artefactWords = java.util.Arrays.asList(a.getName().toLowerCase().split("\\s+"));

            for (String keyword : keywords) {
                if (!keyword.isEmpty() && artefactWords.contains(keyword)) {
                    return a;
                }
            }
        }
        return null;
    }

    /**
     * Finds furniture using fuzzy matching.
     */
    private Furniture findFuzzyMatchFurniture(String targetItemName, Location locationOrPlayer) {
        String[] keywords = targetItemName.toLowerCase().split("\\s+");

        for (Furniture f : locationOrPlayer.getFurniture()) {
            java.util.List<String> furnitureWords = java.util.Arrays.asList(f.getName().toLowerCase().split("\\s+"));

            for (String keyword : keywords) {
                if (!keyword.isEmpty() && furnitureWords.contains(keyword)) {
                    return f;
                }
            }
        }
        return null;
    }

    /**
     * Normalizes an item name by stripping common articles from the beginning.
     */
    private String extractAndNormalizeItemName(String itemName) {
        String lowerName = itemName.toLowerCase();

        for (String article : ARTICLES) {
            if (lowerName.startsWith(article)) {
                return itemName.substring(article.length()).trim();
            }
        }

        return itemName.trim();
    }
}