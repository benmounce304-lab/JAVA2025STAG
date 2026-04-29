package edu.uob.GameEngine;

/**
 * Represents the result of a command execution in the game engine.
 * Contains information about whether the command was successful and any relevant messages.
 */
public class CommandResult {
    private final boolean success;
    private final String message;

    public CommandResult(boolean success, String message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        this.success = success;
        this.message = message;
    }

    public static CommandResult success(String message) {
        return new CommandResult(true, message);
    }

    public static CommandResult failure(String message) {
        return new CommandResult(false, message);
    }

    public String getMessage() {
        return message;
    }

    /**
     * Returns a string representation of the command result.
     *
     * @return A formatted string with success status and message
     */
    @Override
    public String toString() {
        return "CommandResult{" + "success=" + success + ", message='" + message + '\'' + '}';
    }
}

