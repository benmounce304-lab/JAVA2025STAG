package edu.uob.GameEngine;

import edu.uob.GameCommands.DynamicCMD;
import edu.uob.GameCommands.PlayerCMD;
import edu.uob.GameEntities.Player;

import java.util.Map;

/**
 * Controls the parsing and execution of player commands.
 * Acts as the coordinator between the tokenizer, command handlers, and game model.
 * Routes commands to appropriate handlers using the CommandFactory.
 * Supports both strict command matching and flexible natural language parsing.
 */
public class GameController {

    private final GameModel model;

    /**
     * Creates a new GameController with the specified model.
     * @param model The game model containing all game state
     */
    public GameController(GameModel model) {
        this.model = model;
    }

    /**
     * Parses and executes a player command.
     * Handles player creation and delegates to appropriate command handlers.
     * Supports flexible command matching for natural language input.
     *
     * @param command The raw command string (format: "PlayerName: command")
     * @return The result message from executing the command
     */
    public String parseCommand(String command) {
        GameTokenizer tokenizer = new GameTokenizer(command);
        if (!tokenizer.isValid()) {
            return tokenizer.getErrorMessage();
        }

        String playerName = tokenizer.getPlayerName();
        String rawCommand = tokenizer.getRawCommand();

        Player currentPlayer = retrieveOrCreatePlayer(playerName);

        if (CommandFactory.isHealthCommand(rawCommand)) {
            return "You currently have " + currentPlayer.getHealth() + " health.";
        }

        PlayerCMD commandHandler = CommandFactory.createCommandHandler(rawCommand);
        if (commandHandler == null) {
            commandHandler = findFlexibleCommandMatch(rawCommand);
        }

        if (commandHandler == null) {
            commandHandler = new DynamicCMD(rawCommand);
        }

        CommandResult result = commandHandler.execute(currentPlayer, model);
        return result.getMessage();
    }

    /**
     * Attempts to match commands that appear anywhere in the sentence.
     * Supports natural language input like "please can you get the axe"
     *
     * @param rawCommand The raw command string
     * @return A PlayerCMD if a keyword match is found, null otherwise
     */
    private PlayerCMD findFlexibleCommandMatch(String rawCommand) {
        // Check if command contains " get " keyword with word boundaries
        if (matchesKeywordPattern(rawCommand, "get")) {
            String itemPart = extractAfterKeyword(rawCommand, "get");
            if (!itemPart.isEmpty()) {
                return CommandFactory.createCommandHandler("get " + itemPart);
            }
        }

        // Check if command contains " drop " keyword
        if (matchesKeywordPattern(rawCommand, "drop")) {
            String itemPart = extractAfterKeyword(rawCommand, "drop");
            if (!itemPart.isEmpty()) {
                return CommandFactory.createCommandHandler("drop " + itemPart);
            }
        }

        // Check if command contains " goto " keyword
        if (matchesKeywordPattern(rawCommand, "goto")) {
            String destination = extractAfterKeyword(rawCommand, "goto");
            if (!destination.isEmpty()) {
                return CommandFactory.createCommandHandler("goto " + destination);
            }
        }

        // No flexible match found
        return null;
    }

    /**
     * Checks if the command contains a keyword as a complete word (not as a substring).
     *
     * @param command The command string
     * @param keyword The keyword to search for
     * @return true if the keyword appears as a complete word in the command
     */
    private boolean matchesKeywordPattern(String command, String keyword) {
        String pattern = "\\b" + keyword + "\\b";
        return command.matches(".*" + pattern + ".*");
    }

    /**
     * Extracts the part of the command after a given keyword.
     * For example, extractAfterKeyword("please get the axe", "get") returns "the axe"
     *
     * @param command The command string
     * @param keyword The keyword to search after
     * @return The substring after the keyword, or empty string if not found
     */
    private String extractAfterKeyword(String command, String keyword) {
        String pattern = ".*\\b" + keyword + "\\s+(.*)";
        if (command.matches(pattern)) {
            return command.replaceAll(pattern, "$1").trim();
        }
        return "";
    }

    /**
     * Retrieves an existing player by name or creates a new player if not found.
     * New players start in the game's starting room.
     * @param playerName The name of the player
     * @return The Player object (existing or newly created)
     */
    private Player retrieveOrCreatePlayer(String playerName) {
        Map<String, Player> allPlayers = model.getAllPlayers();
        Player currentPlayer = allPlayers.get(playerName);

        if (currentPlayer == null) {
            currentPlayer = new Player(playerName, "A new player");
            currentPlayer.setCurrentLocation(model.getStartingRoom());
            model.addPlayer(playerName, currentPlayer);
        }

        return currentPlayer;
    }
}
