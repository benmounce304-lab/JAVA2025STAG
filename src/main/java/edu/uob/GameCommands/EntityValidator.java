package edu.uob.GameCommands;

import edu.uob.GameEngine.GameAction;
import edu.uob.GameEntities.GameEntity;
import edu.uob.GameEntities.Location;
import edu.uob.GameEntities.Player;
import edu.uob.GameExceptions.MissingEntityException;
import edu.uob.GameExceptions.ExtraneousEntityException;
import edu.uob.GameExceptions.GameException;

import java.util.ArrayList;

public class EntityValidator {

    public static void validateRequiredSubjects(GameAction matchedAction, String command, Player player, Location currentRoom) throws GameException {
        for (String subject : matchedAction.getSubjects()) {

            if (!command.contains(subject.toLowerCase())) {
                throw new GameException("What do you want to interact with? You need to mention '" + subject + "' in your command.");
            }

            boolean found = player.hasItem(subject);
            if (currentRoom.findArtefactByName(subject) != null) found = true;
            if (currentRoom.findFurnitureByName(subject) != null) found = true;
            if (currentRoom.findCharacterByName(subject) != null) found = true;

            if (!found) {
                throw new MissingEntityException(subject);
            }
        }
    }

    public static void validateNoExtraneousEntities(GameAction matchedAction, String command, Player player, Location currentRoom) throws ExtraneousEntityException {
        ArrayList<GameEntity> availableThings = new ArrayList<>();
        availableThings.addAll(player.getInventory());
        availableThings.addAll(currentRoom.getArtefacts());
        availableThings.addAll(currentRoom.getFurniture());
        availableThings.addAll(currentRoom.getCharacters());

        for (GameEntity entity : availableThings) {
            if (command.contains(entity.getName().toLowerCase())) {
                boolean isRequired = false;
                for (String subject : matchedAction.getSubjects()) {
                    if (entity.getName().equalsIgnoreCase(subject)) {
                        isRequired = true;
                        break;
                    }
                }
                if (!isRequired) {
                    throw new ExtraneousEntityException(entity.getName());
                }
            }
        }
    }
}