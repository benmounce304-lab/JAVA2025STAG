package edu.uob.GameCommands;

import edu.uob.GameEntities.Artefact;
import edu.uob.GameEntities.Character;
import edu.uob.GameEntities.Furniture;
import edu.uob.GameEntities.Location;
import edu.uob.GameEntities.Player;
import edu.uob.GameEngine.CommandResult;
import edu.uob.GameEngine.GameModel;

import java.util.Map;

/**
 * Command to look around the current location, providing a description and listing visible artefacts, furniture, and characters.
 */
public class LookCMD implements PlayerCMD {
    private static final String LOOK_DESCRIPTION = "You are in %s.\n";

    /**
     * Executes the LOOK command to look around the current location.
     * @param player The player executing the command
     * @param model The game model containing game state
     * @return CommandResult containing the response to the player
     */
    @Override
    public CommandResult execute(Player player, GameModel model) {
        Location currentRoom = player.getCurrentLocation();
        StringBuilder response = new StringBuilder();

        response.append(String.format(LOOK_DESCRIPTION, currentRoom.getDescription()));

        if (!currentRoom.getArtefacts().isEmpty()) {
            response.append("You see the following artefacts:\n");
            for (Artefact a : currentRoom.getArtefacts()) {
                response.append("- ").append(a.getName()).append(" (").append(a.getDescription()).append(")\n");
            }
        }

        if (!currentRoom.getFurniture().isEmpty()) {
            response.append("You see the following furniture:\n");
            for (Furniture f : currentRoom.getFurniture()) {
                response.append("- ").append(f.getName()).append(" (").append(f.getDescription()).append(")\n");
            }
        }

        if (!currentRoom.getCharacters().isEmpty()) {
            response.append("You see the following characters:\n");
            for (Character c : currentRoom.getCharacters()) {
                response.append("- ").append(c.getName()).append(" (").append(c.getDescription()).append(")\n");
            }
        }

        boolean foundOtherPlayers = false;
        for (Player otherPlayer : model.getAllPlayers().values()) {
            if (otherPlayer.getCurrentLocation() == currentRoom && otherPlayer != player) {
                if (!foundOtherPlayers) {
                    response.append("Other players here:\n");
                    foundOtherPlayers = true;
                }
                response.append("- ").append(otherPlayer.getName()).append("\n");
            }
        }

        if (!currentRoom.getPaths().isEmpty()) {
            response.append("You see paths to the following locations:\n");
            for (Map.Entry<String, Location> entry : currentRoom.getPaths().entrySet()) {
                response.append("- ").append(entry.getKey()).append(" (").append(entry.getValue().getName()).append(")\n");
            }
        }
        return CommandResult.success(response.toString());
    }
}