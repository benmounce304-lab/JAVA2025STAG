package edu.uob.GameEngine;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Consumer; // <-- NEW IMPORT!

public class ActionParser {
    public ArrayList<GameAction> parseActions(File actionsFile) throws Exception {
        ArrayList<GameAction> parsedActions = new ArrayList<>();

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(actionsFile);

        NodeList actionsList = document.getElementsByTagName("action");
        for (int i = 0; i < actionsList.getLength(); i++) {
            Element actionElement = (Element) actionsList.item(i);
            GameAction newAction = new GameAction();

            Element triggersElement = (Element) actionElement.getElementsByTagName("triggers").item(0);
            if (triggersElement != null) {
                NodeList keyphrases = triggersElement.getElementsByTagName("keyphrase");
                for (int j = 0; j < keyphrases.getLength(); j++) {
                    newAction.addTrigger(keyphrases.item(j).getTextContent().toLowerCase(Locale.ROOT));
                }
            }

            parseAndAddEntities(actionElement, "subjects", newAction::addSubject);
            parseAndAddEntities(actionElement, "consumed", newAction::addConsumed);
            parseAndAddEntities(actionElement, "produced", newAction::addProduced);

            Element narrationElement = (Element) actionElement.getElementsByTagName("narration").item(0);
            if (narrationElement != null) {
                newAction.setNarration(narrationElement.getTextContent());
            }

            parsedActions.add(newAction);
        }
        System.out.println("Loaded " + parsedActions.size() + " actions");
        return parsedActions;
    }

    private void parseAndAddEntities(Element actionElement, String groupName, Consumer<String> addMethod) {
        Element groupElement = (Element) actionElement.getElementsByTagName(groupName).item(0);
        if (groupElement != null) {
            NodeList entities = groupElement.getElementsByTagName("entity");
            for (int j = 0; j < entities.getLength(); j++) {
                addMethod.accept(entities.item(j).getTextContent().toLowerCase(Locale.ROOT));
            }
        }
    }
}