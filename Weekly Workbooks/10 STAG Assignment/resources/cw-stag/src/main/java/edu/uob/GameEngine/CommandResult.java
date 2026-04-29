package edu.uob.GameEngine;

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

    @Override
    public String toString() {
        return "CommandResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}

