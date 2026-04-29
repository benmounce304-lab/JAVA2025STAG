package edu.uob.GameCommands;

import edu.uob.GameEntities.Artefact;
import edu.uob.GameEngine.CommandResult;
import edu.uob.GameEngine.GameModel;
import edu.uob.GameEntities.Player;

/**
 * Inventory command handler. Displays all items carried by the player.
 * Known as inv or inventory
 */
public class InvCMD implements PlayerCMD {

    /**
     * Executes inventory to display the player's inventory.
     * @param player The player executing the command
     * @param model The game model
     * @return A CommandResult listing all items in the player's inventory
     */
    @Override
    public CommandResult execute(Player player, GameModel model) {
        if (player.getInventory().isEmpty()) {
            return CommandResult.failure("You have no items in your inventory.");
        }

        StringBuilder response = new StringBuilder();
        response.append("You are carrying:\n");
        for (Artefact a : player.getInventory()) {
            response.append("- ").append(a.getName()).append(" (").append(a.getDescription()).append(")\n");
        }

        return CommandResult.success(response.toString());
    }
}
