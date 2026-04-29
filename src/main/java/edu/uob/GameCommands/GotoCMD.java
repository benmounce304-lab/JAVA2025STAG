package edu.uob.GameCommands;

import edu.uob.GameEntities.Location;
import edu.uob.GameEngine.CommandResult;
import edu.uob.GameEngine.GameModel;
import edu.uob.GameEntities.Player;

/**
 * Goto allows players to move between connected locations.
 * Can only travel to locations that paths directly connect.
 * Player automatically looks around the new location after moving,
 * showing its description and contents.
 */
public class GotoCMD implements PlayerCMD {
    private final String rawCommand;

    /**
     * Creates a new GotoCMD with the raw command string.
     *
     * @param rawCommand The command string starting with "goto "
     */
    public GotoCMD(String rawCommand) {
        this.rawCommand = rawCommand;
    }

    /**
     * Executes the GOTO command to move the player to a connected location.
     * Shows the new location using the LOOK command after a successful movement.
     *
     * @param player The player executing the command
     * @param model  The game model
     * @return CommandResult indicating success or failure
     */
    @Override
    public CommandResult execute(Player player, GameModel model) {
        Location currentRoom = player.getCurrentLocation();
        String lowerCmd = rawCommand.toLowerCase();

        int matchCount = 0;
        String matchedPath = null;

        for (String path : currentRoom.getPaths().keySet()) {
            if (lowerCmd.matches(".*\\b" + path.toLowerCase() + "\\b.*")) {
                matchCount++;
                matchedPath = path;
            }
        }

        if (matchCount > 1) {
            return CommandResult.failure("You can't go in two directions at once. Be more specific!");
        } else if (matchCount == 0) {
            return CommandResult.failure("You cannot go there from here.");
        }

        Location newLocation = model.getAllLocations().get(matchedPath);

        if (newLocation != null) {
            player.setCurrentLocation(newLocation);
            return CommandResult.success("You travel to the " + matchedPath + ".\n" + newLocation.getDescription());
        }

        return CommandResult.failure("That location doesn't seem to exist.");
    }
}