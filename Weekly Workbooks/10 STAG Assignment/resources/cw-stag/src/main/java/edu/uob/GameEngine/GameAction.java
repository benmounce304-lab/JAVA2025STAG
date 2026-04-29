package edu.uob.GameEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class GameAction
{
    private final List<String> triggers;
    private final List<String> subjects;
    private final List<String> consumed;
    private final List<String> produced;
    private String narration;

    public GameAction() {
        this.triggers = new ArrayList<>();
        this.subjects = new ArrayList<>();
        this.consumed = new ArrayList<>();
        this.produced = new ArrayList<>();
        this.narration = "";
    }

    public List<String> getTriggers() {
        return Collections.unmodifiableList(triggers);
    }

    public List<String> getSubjects() {
        return Collections.unmodifiableList(subjects);
    }

    public List<String> getConsumed() {
        return Collections.unmodifiableList(consumed);
    }

    public List<String> getProduced() {
        return Collections.unmodifiableList(produced);
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public void addTrigger(String trigger) { this.triggers.add(trigger);}
    public void addSubject(String subject) { this.subjects.add(subject);}
    public void addConsumed(String consumed) { this.consumed.add(consumed);}
    public void addProduced(String produced) { this.produced.add(produced);}
}
