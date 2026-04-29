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
     * @param rawCommand The command string starting with "goto "
     */
    public GotoCMD(String rawCommand) {
        this.rawCommand = rawCommand;
    }

    /**
     * Executes the GOTO command to move the player to a connected location.
     * Shows the new location using the LOOK command after a successful movement.
     * @param player The player executing the command
     * @param model The game model
     * @return CommandResult indicating success or failure
     */
    @Override
    public CommandResult execute(Player player, GameModel model) {
        String destinationName = rawCommand.substring(5).trim();
        Location currentRoom = player.getCurrentLocation();

        if (currentRoom.getPaths().containsKey(destinationName)) {
            Location newRoom = currentRoom.getPaths().get(destinationName);
            player.setCurrentLocation(newRoom);

            LookCMD look = new LookCMD();
            CommandResult lookResult = look.execute(player, model);
            String message = "You travel to the " + destinationName + ".\n" + lookResult.getMessage();
            return CommandResult.success(message);
        }

        return CommandResult.failure("You cannot go to " + destinationName + " from here.");
    }
}