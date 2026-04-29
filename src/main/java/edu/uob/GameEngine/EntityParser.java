package edu.uob.GameEngine;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import edu.uob.GameEntities.Artefact;
import edu.uob.GameEntities.Character;
import edu.uob.GameEntities.Furniture;
import edu.uob.GameEntities.Location;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Parses entity definitions from a Graphviz file and constructs game entities.
 * Handles locations, artefacts, furniture, and characters.
 */

public class EntityParser {

    private Location startingRoom = null;

    public HashMap<String, Location> parseEntities(File entitiesFile) throws Exception {
        HashMap<String, Location> parsedLocations = new HashMap<>();

        Parser parser = new Parser();
        parser.parse(new FileReader(entitiesFile));

        if (parser.getGraphs().isEmpty()) {
            throw new Exception("No graphs found in the entity file");
        }

        Graph wholeDocument = parser.getGraphs().get(0);
        ArrayList<Graph> sections = wholeDocument.getSubgraphs();

        ArrayList<Graph> locations = sections.get(0).getSubgraphs();
        for (Graph g : locations) {
            Node locationDetails = g.getNodes(false).get(0);
            String roomName = locationDetails.getId().getId();
            String roomDesc = locationDetails.getAttribute("description");
            Location newRoom = new Location(roomName, roomDesc);

            parsedLocations.put(roomName, newRoom);

            if (this.startingRoom == null) {
                this.startingRoom = newRoom;
                System.out.println("Starting room: " + roomName);
            }

            ArrayList<Graph> roomContents = g.getSubgraphs();
            for (Graph contentGraph : roomContents) {
                String entityType = contentGraph.getId().getId();

                ArrayList<Node> entities = contentGraph.getNodes(false);
                for (Node n : entities) {
                    String entityName = n.getId().getId();
                    String entityDesc = n.getAttribute("description");
                    switch (entityType) {
                        case "artefacts" -> {
                            Artefact a = new Artefact(entityName, entityDesc);
                            newRoom.addArtefact(a);
                        }
                        case "furniture" -> {
                            Furniture f = new Furniture(entityName, entityDesc);
                            newRoom.addFurniture(f);
                        }
                        case "characters" -> {
                            edu.uob.GameEntities.Character c = new Character(entityName, entityDesc);
                            newRoom.addCharacter(c);
                        }
                        default -> System.err.println("Unknown entity type: " + entityType);
                    }
                }
            }
        }

        ArrayList<Edge> paths = sections.get(1).getEdges();
        for (Edge e : paths) {
            Node fromLocation = e.getSource().getNode();
            String fromName = fromLocation.getId().getId();
            Node toLocation = e.getTarget().getNode();
            String toName = toLocation.getId().getId();

            Location sourceRoom = parsedLocations.get(fromName);
            Location destRoom = parsedLocations.get(toName);

            if (sourceRoom != null && destRoom != null) {
                sourceRoom.addPath(toName, destRoom);
            } else {
                System.err.println("Invalid location reference: " + fromName + " or " + toName);
            }
        }
        return parsedLocations;
    }

    public Location getStartingRoom() {
        return startingRoom;
    }
}