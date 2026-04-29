package edu.uob.GameCommands;

import edu.uob.GameEngine.CommandResult;
import edu.uob.GameEngine.GameAction;
import edu.uob.GameEngine.GameModel;
import edu.uob.GameEntities.Location;
import edu.uob.GameEntities.Player;
import edu.uob.GameExceptions.AmbiguousActionException;
import edu.uob.GameExceptions.ExtraneousEntityException;
import edu.uob.GameExceptions.MissingEntityException;
import edu.uob.GameExceptions.GameException;

/**
 * Handles all dynamic game actions defined in the action XML file.
 * Validates entities, consumes required items, and produces results.
 */
public class DynamicCMD implements PlayerCMD {
    private final String rawCommand;

    public DynamicCMD(String rawCommand) {
        this.rawCommand = rawCommand;
    }

    @Override
    public CommandResult execute(Player player, GameModel model) {
        try {
            GameAction matchedAction = ActionMatcher.findMatchingAction(rawCommand, model);
            if (matchedAction == null) {
                return CommandResult.failure("I don't understand that command.");
            }

            Location currentRoom = player.getCurrentLocation();

            EntityValidator.validateNoExtraneousEntities(matchedAction, rawCommand, player, currentRoom);
            EntityValidator.validateRequiredSubjects(matchedAction, rawCommand, player, currentRoom);

            String deathMessage = ItemConsumer.consumeItems(player, matchedAction, currentRoom, model);
            if (deathMessage != null) {
                return CommandResult.failure(deathMessage);
            }

            ItemProducer.produceItems(player, matchedAction, currentRoom, model);
            return CommandResult.success(matchedAction.getNarration());

        } catch (AmbiguousActionException e) {
            return CommandResult.failure("Be more specific. The command is ambiguous.");
        } catch (MissingEntityException e) {
            return CommandResult.failure("You do not have the required item to do that. You are missing a: " + e.getEntityName());
        } catch (ExtraneousEntityException e) {
            return CommandResult.failure("You cannot use the " + e.getEntityName() + " like that.");
        } catch (GameException e) {
            return CommandResult.failure(e.getMessage());
        }
    }
}