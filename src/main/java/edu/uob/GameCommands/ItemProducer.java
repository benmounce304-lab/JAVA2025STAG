package edu.uob.GameCommands;

import edu.uob.GameEngine.GameAction;
import edu.uob.GameEngine.GameModel;
import edu.uob.GameEntities.Artefact;
import edu.uob.GameEntities.Character;
import edu.uob.GameEntities.Furniture;
import edu.uob.GameEntities.Location;
import edu.uob.GameEntities.Player;

/**
 * Produces items based on the matched action.
 * Handles health and moving to other locations.
 */

public class ItemProducer {
    private static final String HEALTH_KEYWORD = "health";
    private static final int HEAL_AMOUNT = 1;

    public static void produceItems(Player player, GameAction matchedAction, Location currentRoom, GameModel model) {
        for (String producedItem : matchedAction.getProduced()) {
            if (producedItem.equalsIgnoreCase(HEALTH_KEYWORD)) {
                player.heal(HEAL_AMOUNT);
            } else if (model.getAllLocations().containsKey(producedItem)) {
                Location dest = model.getAllLocations().get(producedItem);
                currentRoom.addPath(producedItem, dest);
            } else {
                moveEntityToRoom(producedItem, currentRoom, model);
            }
        }
    }

    /**
     * Moves an entity to a new room.
     */
    private static void moveEntityToRoom(String entityName, Location targetRoom, GameModel model) {
        for (Location loc : model.getAllLocations().values()) {
            for (Artefact a : loc.getArtefacts()) {
                if (a.getName().equalsIgnoreCase(entityName)) {
                    loc.removeArtefact(a);
                    targetRoom.addArtefact(a);
                    return;
                }
            }

            for (Furniture f : loc.getFurniture()) {
                if (f.getName().equalsIgnoreCase(entityName)) {
                    loc.removeFurniture(f);
                    targetRoom.addFurniture(f);
                    return;
                }
            }

            for (Character c : loc.getCharacters()) {
                if (c.getName().equalsIgnoreCase(entityName)) {
                    loc.removeCharacter(c);
                    targetRoom.addCharacter(c);
                    return;
                }
            }
        }
    }
}

