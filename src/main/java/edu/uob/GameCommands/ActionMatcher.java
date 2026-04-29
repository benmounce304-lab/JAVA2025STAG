package edu.uob.GameCommands;

import edu.uob.GameEngine.GameAction;
import edu.uob.GameEngine.GameModel;
import edu.uob.GameExceptions.AmbiguousActionException;

import java.util.HashSet;
import java.util.Set;

public class ActionMatcher {

    public static GameAction findMatchingAction(String command, GameModel model) throws AmbiguousActionException {
        if (command == null || model == null) {
            throw new IllegalArgumentException("Command and model cannot be null");
        }

        Set<GameAction> matchingActions = new HashSet<>();

        for (GameAction action : model.getAllActions()) {
            for (String trigger : action.getTriggers()) {
                if (trigger != null && command.contains(trigger)) {
                    matchingActions.add(action);
                    break;
                }
            }
        }

        if (matchingActions.isEmpty()) {
            return null;
        }

        if (matchingActions.size() > 1) {
            throw new AmbiguousActionException("Ambiguous command: multiple actions match the command.");
        }

        return matchingActions.iterator().next();
    }
}