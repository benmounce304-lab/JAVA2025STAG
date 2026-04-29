package edu.uob.GameCommands;

import edu.uob.GameEngine.CommandResult;
import edu.uob.GameEngine.GameModel;
import edu.uob.GameEntities.Player;

/**
 * Interface for player-related commands in the game.
 * Defines the contract for all player-specific commands.
 * Each command handler should implement this interface.
 */
public interface PlayerCMD {
    CommandResult execute(Player player, GameModel model);
}


