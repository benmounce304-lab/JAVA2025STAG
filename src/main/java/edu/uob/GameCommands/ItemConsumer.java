package edu.uob.GameCommands;

import edu.uob.GameEngine.CommandResult;
import edu.uob.GameEngine.GameAction;
import edu.uob.GameEngine.GameModel;
import edu.uob.GameEntities.Artefact;
import edu.uob.GameEntities.Furniture;
import edu.uob.GameEntities.Location;
import edu.uob.GameEntities.Player;

/**
 * Consumes items based on the matched action.
 * Handles health and moving to other locations.
 * Handles death and moving to the start room.
 */

public class ItemConsumer {
    private static final String STORAGE_LOCATION = "storeroom";
    private static final String HEALTH_KEYWORD = "health";
    private static final int MAX_HEALTH = 3;
    private static final int DAMAGE_AMOUNT = 1;

    public static String consumeItems(Player player, GameAction matchedAction, Location currentRoom, GameModel model) {
        Location storeroom = model.getAllLocations().get(STORAGE_LOCATION);

        for (String consumedItem : matchedAction.getConsumed()) {
            if (consumedItem.equalsIgnoreCase(HEALTH_KEYWORD)) {
                player.damage(DAMAGE_AMOUNT);
                if (player.getHealth() <= 0) {
                    return handleDeath(player, model);
                }
                continue;
            }

            boolean wasConsumed = false;

            for (Artefact a : player.getInventory()) {
                if (a.getName().equalsIgnoreCase(consumedItem)) {
                    player.removeFromInventory(a);
                    storeroom.addArtefact(a);
                    wasConsumed = true;
                    break;
                }
            }
            if (wasConsumed) continue;

            for (Artefact a : currentRoom.getArtefacts()) {
                if (a.getName().equalsIgnoreCase(consumedItem)) {
                    currentRoom.removeArtefact(a);
                    storeroom.addArtefact(a);
                    wasConsumed = true;
                    break;
                }
            }
            if (wasConsumed) continue;

            for (Furniture f : currentRoom.getFurniture()) {
                if (f.getName().equalsIgnoreCase(consumedItem)) {
                    currentRoom.removeFurniture(f);
                    storeroom.addFurniture(f);
                    break;
                }
            }
        }
        return null;
    }

    /**
     * Moves the player to the start room and transfers all items to the start room.
     */
    private static String handleDeath(Player player, GameModel model) {
        Location deathRoom = player.getCurrentLocation();

        for (Artefact a : new java.util.ArrayList<>(player.getInventory())) {
            deathRoom.addArtefact(a);
            player.removeFromInventory(a);
        }

        player.setHealth(MAX_HEALTH);
        player.setCurrentLocation(model.getStartingRoom());

        LookCMD look = new LookCMD();
        CommandResult lookResult = look.execute(player, model);
        return "You died and lost all of your items. You awaken back in the start room.\n" + lookResult.getMessage();
    }
}

