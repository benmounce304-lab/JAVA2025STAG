package edu.uob;

import java.util.ArrayList;

public class GameAction
{
    private ArrayList<String> triggers;
    private ArrayList<String> subjects;
    private ArrayList<String> consumed;
    private ArrayList<String> produced;
    private String narration;

    public GameAction() {
        this.triggers = new ArrayList<>();
        this.subjects = new ArrayList<>();
        this.consumed = new ArrayList<>();
        this.produced = new ArrayList<>();
        this.narration = "";
    }

    public ArrayList<String> getTriggers() {
        return triggers;
    }

    public void setTriggers(ArrayList<String> triggers) {
        this.triggers = triggers;
    }

    public ArrayList<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(ArrayList<String> subjects) {
        this.subjects = subjects;
    }

    public ArrayList<String> getConsumed() {
        return consumed;
    }

    public void setConsumed(ArrayList<String> consumed) {
        this.consumed = consumed;
    }

    public ArrayList<String> getProduced() {
        return produced;
    }

    public void setProduced(ArrayList<String> produced) {
        this.produced = produced;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public boolean matchesTrigger(String command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }
        return triggers.contains(command);
    }

    public boolean requiresSubject(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Subject name cannot be null");
        }
        return !subjects.isEmpty();
    }

    public boolean hasConsumed(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Subject name cannot be null");
        }
        return consumed.contains(name);
    }
}
