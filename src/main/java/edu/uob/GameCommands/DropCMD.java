package edu.uob.GameCommands;

import edu.uob.GameEntities.Artefact;
import edu.uob.GameEntities.Location;
import edu.uob.GameEngine.CommandResult;
import edu.uob.GameEngine.GameModel;
import edu.uob.GameEntities.Player;

/**
 * The DROP command - allows players to drop items from their inventory into the current location.
 * Dropped items become available to other players and persist in the location.
 * Supports both exact matching and fuzzy matching (e.g., "drop razor sharp axe" finds "axe").
 */
public class DropCMD implements PlayerCMD {
    private static final String[] ARTICLES = {"the ", "a ", "an "};
    private final String rawCommand;
    private static final int MAX_COMMAND = 5;

    /**
     * Creates a new DropCMD with the raw command string.
     */
    public DropCMD(String rawCommand) {
        this.rawCommand = rawCommand;
    }

    /**
     * Executes the DROP command to place an artefact from the player's inventory into the current location.
     * First tries exact name matching, then falls back to fuzzy matching.
     *
     * @param player The player executing the command
     * @param model  The game model
     * @return CommandResult indicating success or failure
     */
    @Override
    public CommandResult execute(Player player, GameModel model) {
        if (rawCommand.length() <= MAX_COMMAND) {
            return CommandResult.failure("You must specify an item to drop.");
        }
        String targetItemName = removeArticles(rawCommand.substring(5).trim());
        Location currentRoom = player.getCurrentLocation();

        for (Artefact a : player.getInventory()) {
            if (a.getName().equalsIgnoreCase(targetItemName)) {
                player.removeFromInventory(a);
                currentRoom.addArtefact(a);
                return CommandResult.success("You dropped the " + a.getName() + ".");
            }
        }

        Artefact keywordMatch = findItemByKeyword(targetItemName, player);
        if (keywordMatch != null) {
            player.removeFromInventory(keywordMatch);
            currentRoom.addArtefact(keywordMatch);
            return CommandResult.success("You dropped the " + keywordMatch.getName() + ".");
        }
        return CommandResult.failure("You are not carrying " + targetItemName + ".");
    }

    /**
     * Finds an artefact in the player's inventory using fuzzy matching.
     * Matches if any word from the target appears in the artefact's name.
     */
    private Artefact findItemByKeyword(String targetItemName, Player player) {
        String[] keywords = targetItemName.toLowerCase().split("\\s+");

        for (Artefact a : player.getInventory()) {
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
     * Normalizes an item name by stripping common articles from the beginning.
     * For example: "the axe" → "axe", "a sword" → "sword", "an apple" → "apple"
     */
    private String removeArticles(String itemName) {
        String lowerName = itemName.toLowerCase();

        for (String article : ARTICLES) {
            if (lowerName.startsWith(article)) {
                return itemName.substring(article.length()).trim();
            }
        }
        return itemName;
    }
}