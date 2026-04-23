package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public final class GameServer {

    private static final char END_OF_TRANSMISSION = 4;
    private final HashMap<String, Location> allLocations;
    private Location startingRoom;
    private final ArrayList<GameAction> allActions = new ArrayList<>();
    private final HashMap<String, Player> allPlayers = new HashMap<>();

    public static void main(String[] args) throws IOException {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        server.blockingListenOn(8888);
    }

    /**
     * Do not change the following method signature or we won't be able to mark your submission
     * Instanciates a new server instance, specifying a game with some configuration files
     *
     * @param entitiesFile The game configuration file containing all game entities to use in your game
     * @param actionsFile  The game configuration file containing all game actions to use in your game
     */
    public GameServer(File entitiesFile, File actionsFile) {
        this.allLocations = new HashMap<>();

        try {
            Parser parser = new Parser();
            FileReader reader = new FileReader(entitiesFile);
            parser.parse(reader);

            Graph wholeDocument = parser.getGraphs().get(0);
            ArrayList<Graph> sections = wholeDocument.getSubgraphs();

            ArrayList<Graph> locations = sections.get(0).getSubgraphs();

            for (Graph g : locations) {
                Node locationDetails = g.getNodes(false).get(0);
                String roomName = locationDetails.getId().getId();
                String roomDesc = locationDetails.getAttribute("description");

                Location newRoom = new Location(roomName, roomDesc);

                allLocations.put(roomName, newRoom);

                if (startingRoom == null) {
                    startingRoom = newRoom;
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
                                newRoom.getArtefacts().add(a);
                            }
                            case "furniture" -> {
                                Furniture f = new Furniture(entityName, entityDesc);
                                newRoom.getFurniture().add(f);
                            }
                            case "characters" -> {
                                Character c = new Character(entityName, entityDesc);
                                newRoom.getCharacters().add(c);
                            }
                            default -> {
                                System.err.println("Unknown entity type: " + entityType);
                            }
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

                Location sourceRoom = allLocations.get(fromName);
                Location destRoom = allLocations.get(toName);

                if (sourceRoom != null && destRoom != null) {
                    sourceRoom.getPaths().put(toName, destRoom);
                } else {
                    System.err.println("Invalid location reference: " + fromName + " or " + toName);
                }
            }

        } catch (Exception e) {
            System.err.println("Error parsing game data: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(actionsFile);
            document.getDocumentElement().normalize();

            NodeList actionsList = document.getElementsByTagName("action");

            for (int i = 0; i < actionsList.getLength(); i++) {
                Element actionElement = (Element) actionsList.item(i);

                GameAction newAction = new GameAction();

                Element triggersElement = (Element) actionElement.getElementsByTagName("triggers").item(0);
                if (triggersElement != null) {
                    NodeList keyphrases = triggersElement.getElementsByTagName("keyphrase");
                    for (int j = 0; j < keyphrases.getLength(); j++) {
                        newAction.getTriggers().add(keyphrases.item(j).getTextContent());
                    }
                }

                Element subjectsElement = (Element) actionElement.getElementsByTagName("subjects").item(0);
                if (subjectsElement != null) {
                    NodeList entities = subjectsElement.getElementsByTagName("entity");
                    for (int j = 0; j < entities.getLength(); j++) {
                        newAction.getSubjects().add(entities.item(j).getTextContent());
                    }
                }


                Element consumedElement = (Element) actionElement.getElementsByTagName("consumed").item(0);
                if (consumedElement != null) {
                    NodeList entities = consumedElement.getElementsByTagName("entity");
                    for (int j = 0; j < entities.getLength(); j++) {
                        newAction.getConsumed().add(entities.item(j).getTextContent());
                    }
                }

                Element producedElement = (Element) actionElement.getElementsByTagName("produced").item(0);
                if (producedElement != null) {
                    NodeList entities = producedElement.getElementsByTagName("entity");
                    for (int j = 0; j < entities.getLength(); j++) {
                        newAction.getProduced().add(entities.item(j).getTextContent());
                    }
                }

                Element narrationElement = (Element) actionElement.getElementsByTagName("narration").item(0);
                if (narrationElement != null) {
                    newAction.setNarration(narrationElement.getTextContent());
                }

                allActions.add(newAction);
            }

            System.out.println("Loaded " + allActions.size() + " actions");

        } catch (Exception e) {
            System.err.println("Error parsing game actions: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Do not change the following method signature or we won't be able to mark your submission
     * This method handles all incoming game commands and carries out the corresponding actions.</p>
     *
     * @param command The incoming command to be processed
     */
    public String handleCommand(String command) {
        String[] parts = command.split(":", 2);
        if (parts.length < 2) {
            return "Invalid command format. Use 'Username: command'";
        }

        String playerName = parts[0].trim();

        if (!playerName.matches("^[a-zA-Z \\'-]+$")) {
            return "Invalid name. Player names can only contain letters, spaces, apostrophes, and hyphens.";
        }
        String rawCommand = parts[1].trim().toLowerCase();

        Player currentPlayer = allPlayers.get(playerName);
        if (currentPlayer == null) {
            currentPlayer = new Player(playerName, "A new player");
            currentPlayer.setCurrentLocation(startingRoom);
            allPlayers.put(playerName, currentPlayer);
        }

        if (rawCommand.equalsIgnoreCase("look")) {
            return executeLook(currentPlayer);
        } else if (rawCommand.equalsIgnoreCase("inv") || rawCommand.equalsIgnoreCase("inventory")) {
            return executeInventory(currentPlayer);
        } else if (rawCommand.startsWith("get ")) {
            return executeGet(currentPlayer, rawCommand);
        } else if (rawCommand.startsWith("drop ")) {
            return executeDrop(currentPlayer, rawCommand);
        } else if (rawCommand.startsWith("goto ")) {
            return executeGo(currentPlayer, rawCommand);
        } else if (rawCommand.equalsIgnoreCase("health")) {
            return executeHealth(currentPlayer);
        }
        return executeCustomAction(currentPlayer, rawCommand);
    }

    /**
     * Do not change the following method signature or we won't be able to mark your submission
     * Starts a *blocking* socket server listening for new connections.
     *
     * @param portNumber The port to listen on.
     * @throws IOException If any IO related operation fails.
     */
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    /**
     * Do not change the following method signature or we won't be able to mark your submission
     * Handles an incoming connection from the socket server.
     *
     * @param serverSocket The client socket to read/write from.
     * @throws IOException If any IO related operation fails.
     */
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
             BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if (incomingCommand != null) {
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }

    private String executeLook(Player player) {
        Location currentRoom = player.getCurrentLocation();
        StringBuilder response = new StringBuilder();
        response.append("You are in ").append(currentRoom.getDescription()).append(".\n");
        response.append("You see the following paths:\n");

        if (!currentRoom.getArtefacts().isEmpty()) {
            response.append("You see the following artefacts:\n");
            for (Artefact a : currentRoom.getArtefacts()) {
                response.append("- ").append(a.getName()).append(" (").append(a.getDescription()).append(")\n");
            }
        }

        if (!currentRoom.getFurniture().isEmpty()) {
            response.append("You see the following furniture:\n");
            for (Furniture f : currentRoom.getFurniture()) {
                response.append("- ").append(f.getName()).append(" (").append(f.getDescription()).append(")\n");
            }
        }

        if (!currentRoom.getCharacters().isEmpty()) {
            response.append("You see the following characters:\n");
            for (Character c : currentRoom.getCharacters()) {
                response.append("- ").append(c.getName()).append(" (").append(c.getDescription()).append(")\n");
            }
        }

        boolean foundOtherPlayers = false;
        for (Player otherPlayer : allPlayers.values()) {
            if (otherPlayer.getCurrentLocation() == currentRoom && otherPlayer != player) {
                if (!foundOtherPlayers) {
                    response.append("Other players here:\n");
                    foundOtherPlayers = true;
                }
                response.append("- ").append(otherPlayer.getName()).append("\n");
            }
        }

        if (!currentRoom.getPaths().isEmpty()) {
            response.append("You see paths to the following locations:\n");
            for (Map.Entry<String, Location> entry : currentRoom.getPaths().entrySet()) {
                response.append("- ").append(entry.getKey()).append(" (").append(entry.getValue().getName()).append(")\n");
            }
        }

        return response.toString();
    }

    private String executeInventory(Player player) {
        StringBuilder response = new StringBuilder();
        if (player.getInventory().isEmpty()) {
            return "You have no items in your inventory.";
        }
        response.append("You are carrying:\n");
        for (Artefact a : player.getInventory()) {
            response.append("- ").append(a.getName()).append(" (").append(a.getDescription()).append(")\n");
        }
        return response.toString();
    }

    private String executeGet(Player player, String rawCommand) {
        String targetItemName = rawCommand.substring(4).trim();
        Location currentRoom = player.getCurrentLocation();

        for (int i = 0; i < currentRoom.getArtefacts().size(); i++) {
            Artefact a = currentRoom.getArtefacts().get(i);
            if (a.getName().equalsIgnoreCase(targetItemName)) {
                currentRoom.getArtefacts().remove(i);
                player.getInventory().add(a);
                return "You picked up the " + targetItemName + ".";
            }
        }
        for (Furniture f : currentRoom.getFurniture()) {
            if (f.getName().equalsIgnoreCase(targetItemName)) {
                return "You cannot pick up " + targetItemName + ", it's too heavy for you to carry.";
            }
        }
        return "There is no " + targetItemName + " in this room.";
    }

    private String executeDrop(Player player, String rawCommand) {
        String targetItemName = rawCommand.substring(5).trim();
        for (int i = 0; i < player.getInventory().size(); i++) {
            Artefact a = player.getInventory().get(i);
            if (a.getName().equalsIgnoreCase(targetItemName)) {
                player.getInventory().remove(i);
                player.getCurrentLocation().getArtefacts().add(a);

                return "You dropped the " + targetItemName + ".";
            }
        }
        return "You are not carrying " + targetItemName + ".";
    }

    private String executeGo(Player player, String rawCommand) {
        String destinationName = rawCommand.substring(5).trim();
        Location currentRoom = player.getCurrentLocation();
        if (currentRoom.getPaths().containsKey(destinationName)) {
            Location newRoom = currentRoom.getPaths().get(destinationName);
            player.setCurrentLocation(newRoom);
            return "You travel to the " + destinationName + ".\n" + executeLook(player);
        } else {
            return "You cannot go to " + destinationName + " from here.";
        }
    }

    private String executeCustomAction(Player player, String rawCommand) {
        String[] words = rawCommand.split("\\s+");

        ArrayList<GameAction> matchingActions = new ArrayList<>();
        for (GameAction action : allActions) {
            for (String trigger : action.getTriggers()) {
                if (rawCommand.contains(trigger)) {
                    if (!matchingActions.contains(action)) {
                        matchingActions.add(action);
                    }
                }
            }
        }
        if (matchingActions.isEmpty()) {
            return "I don't understand that command.";
        } else if (matchingActions.size() > 1) {
            return "Be more specific. The command is ambiguous.";
        }

        GameAction matchedAction = matchingActions.get(0);
        Location currentRoom = player.getCurrentLocation();

        ArrayList<GameEntity> availableThings = new ArrayList<>();
        availableThings.addAll(player.getInventory());
        availableThings.addAll(currentRoom.getArtefacts());
        availableThings.addAll(currentRoom.getFurniture());
        availableThings.addAll(currentRoom.getCharacters());

        for (GameEntity entity : availableThings) {
            if (rawCommand.contains(entity.getName().toLowerCase())) {
                boolean isRequired = false;
                for (String subject : matchedAction.getSubjects()) {
                    if (entity.getName().equalsIgnoreCase(subject)) {
                        isRequired = true;
                        break;
                    }
                }

                if (!isRequired) {
                    return "You cannot use the " + entity.getName() + " like that.";
                }
            }
        }

        for (String subject : matchedAction.getSubjects()) {
            boolean found = false;
            for (Artefact a : player.getInventory()) {
                if (a.getName().equalsIgnoreCase(subject)) {
                    found = true;
                    break;
                }
            }
            for (Artefact a : currentRoom.getArtefacts()) {
                if (a.getName().equalsIgnoreCase(subject)) {
                    found = true;
                    break;
                }
            }
            for (Furniture f : currentRoom.getFurniture()) {
                if (f.getName().equalsIgnoreCase(subject)) {
                    found = true;
                    break;
                }
            }
            for (Character c : currentRoom.getCharacters()) {
                if (c.getName().equalsIgnoreCase(subject)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return "You don't have a " + subject + " to " + matchedAction.getNarration();
            }
        }
        Location storeroom = allLocations.get("storeroom");

        for (String consumedItem : matchedAction.getConsumed()) {
            if (consumedItem.equalsIgnoreCase("health")) {
                player.setHealth(player.getHealth() - 1);
                if (player.getHealth() <= 0) {
                    return handleDeath(player);
                }
                continue;
            }

            boolean wasConsumed = false;

            for (int i = 0; i < player.getInventory().size(); i++) {
                if (player.getInventory().get(i).getName().equalsIgnoreCase(consumedItem)) {
                    Artefact a = player.getInventory().remove(i);
                    storeroom.getArtefacts().add(a);
                    wasConsumed = true;
                    break;
                }
            }
            if (wasConsumed) continue;

            for (int i = 0; i < currentRoom.getArtefacts().size(); i++) {
                if (currentRoom.getArtefacts().get(i).getName().equalsIgnoreCase(consumedItem)) {
                    Artefact a = currentRoom.getArtefacts().remove(i);
                    storeroom.getArtefacts().add(a);
                    wasConsumed = true;
                    break;
                }
            }
            if (wasConsumed) continue;

            for (int i = 0; i < currentRoom.getFurniture().size(); i++) {
                if (currentRoom.getFurniture().get(i).getName().equalsIgnoreCase(consumedItem)) {
                    Furniture f = currentRoom.getFurniture().remove(i);
                    storeroom.getFurniture().add(f);
                    break;
                }
            }
        }

        for (String producedItem : matchedAction.getProduced()) {
            if (producedItem.equalsIgnoreCase("health")) {
                player.setHealth(player.getHealth() + 1);
                if (player.getHealth() > 3) {
                    player.setHealth(3);
                }
            } else if (allLocations.containsKey(producedItem)) {
                Location dest = allLocations.get(producedItem);
                currentRoom.getPaths().put(producedItem, dest);
            } else {
                for (Location loc : allLocations.values()) {
                    for (int i = 0; i < loc.getArtefacts().size(); i++) {
                        if (loc.getArtefacts().get(i).getName().equalsIgnoreCase(producedItem)) {
                            Artefact a = loc.getArtefacts().remove(i);
                            currentRoom.getArtefacts().add(a);
                            break;
                        }
                    }
                    for (int i = 0; i < loc.getFurniture().size(); i++) {
                        if (loc.getFurniture().get(i).getName().equalsIgnoreCase(producedItem)) {
                            Furniture f = loc.getFurniture().remove(i);
                            currentRoom.getFurniture().add(f);
                            break;
                        }
                    }
                    for (int i = 0; i < loc.getCharacters().size(); i++) {
                        if (loc.getCharacters().get(i).getName().equalsIgnoreCase(producedItem)) {
                            Character c = loc.getCharacters().remove(i);
                            currentRoom.getCharacters().add(c);
                            break;
                        }
                    }
                }
            }
        }

        return matchedAction.getNarration();
    }

    private String executeHealth(Player player) {
        return "You currently have " + player.getHealth() + " health.";
    }

    private String handleDeath(Player player) {
        Location deathRoom = player.getCurrentLocation();

        for (Artefact a : player.getInventory()) {
            deathRoom.getArtefacts().add(a);
        }
        player.getInventory().clear();
        player.setHealth(3);
        player.setCurrentLocation(startingRoom);

        return "You died and lost all of your items. You awaken back in the start room.\n" + executeLook(player);
    }
}
