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
     * First tries exact name matching, then falls back to fuzzy matching.
     * @param player The player executing the command
     * @param model The game model
     * @return CommandResult indicating success or failure
     */
    @Override
    public CommandResult execute(Player player, GameModel model) {
        String targetItemName = extractAndNormalizeItemName(rawCommand.substring(4).trim());
        Location currentRoom = player.getCurrentLocation();

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
     * Matches if any word from the target appears in the artefact's name.
     * For example: "razor sharp axe" would match an artefact named "axe".
     *
     * @param targetItemName The normalized item name from the command
     * @param locationOrPlayer The location or player whose artefacts to search
     * @return The matching Artefact, or null if no match found
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
     * Matches if any word from the target appears in the furniture's name.
     *
     * @param targetItemName The normalized item name from the command
     * @param locationOrPlayer The location or player to search in
     * @return The matching Furniture, or null if no match found
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
     * For example: "the axe" → "axe", "a sword" → "sword", "an apple" → "apple"
     *
     * @param itemName The raw item name from the command
     * @return The normalized item name without leading articles
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
