package edu.uob.GameEngine;

import edu.uob.GameCommands.*;

/**
 * Factory for creating appropriate command handlers based on raw command strings.
 * Eliminates high IF/ELSE density by centralizing command routing logic.
 * Each command produces a specific handler that executes the command semantics.
 */
public class CommandFactory {

    /**
     * Creates the appropriate PlayerCMD implementation for the given raw command.
     * Returns null if the command should be handled as a dynamic action.
     */
    public static PlayerCMD createCommandHandler(String rawCommand) {
        if (rawCommand == null) {
            return null;
        }

        if (rawCommand.equalsIgnoreCase("look")) {
            return new LookCMD();
        }

        if (rawCommand.equalsIgnoreCase("inv") || rawCommand.equalsIgnoreCase("inventory")) {
            return new InvCMD();
        }

        if (rawCommand.startsWith("get ")) {
            return new GetCMD(rawCommand);
        }

        if (rawCommand.startsWith("drop ")) {
            return new DropCMD(rawCommand);
        }

        if (rawCommand.startsWith("goto ")) {
            return new GotoCMD(rawCommand);
        }
        return null;
    }

    /**
     * Checks if the raw command is the special "health" command.
     */
    public static boolean isHealthCommand(String rawCommand) {
        return rawCommand != null && rawCommand.equalsIgnoreCase("health");
    }
}

