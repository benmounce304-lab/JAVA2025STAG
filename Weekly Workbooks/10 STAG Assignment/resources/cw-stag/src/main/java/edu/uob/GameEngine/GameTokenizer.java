package edu.uob.GameEngine;

public class GameTokenizer {

    private final String playerName;
    private final String rawCommand;
    private final boolean isValid;
    private final String errorMessage;

    public GameTokenizer(String fullCommand) {
        if (fullCommand == null) {
            this.isValid = false;
            this.errorMessage = "Command cannot be null.";
            this.playerName = null;
            this.rawCommand = null;
            return;
        }

        String[] parts = fullCommand.split(":", 2);
        if (parts.length < 2) {
            this.isValid = false;
            this.errorMessage = "Invalid command format. Use 'Username: command'";
            this.playerName = null;
            this.rawCommand = null;
            return;
        }

        String tempPlayerName = parts[0].trim();
        if (!tempPlayerName.matches("^[a-zA-Z '-]+$")) {
            this.isValid = false;
            this.errorMessage = "Invalid name. Player names can only contain letters, spaces, apostrophes, and hyphens.";
            this.playerName = null;
            this.rawCommand = null;
            return;
        }

        this.playerName = tempPlayerName;
        this.rawCommand = parts[1].trim().replaceAll("\\s+", " ").toLowerCase();
        this.isValid = true;
        this.errorMessage = null;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getRawCommand() {
        return rawCommand;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}