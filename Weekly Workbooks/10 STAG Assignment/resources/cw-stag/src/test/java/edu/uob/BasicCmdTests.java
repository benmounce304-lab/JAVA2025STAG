package edu.uob;

import edu.uob.GameExceptions.GameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.nio.file.Paths;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

final class BasicCmdTests {
    private GameServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    @Test
    void testCommandWithSymbols() throws GameException {
        String response = server.handleCommand("Simon: look").toLowerCase();
        assertTrue(response.contains("a log cabin"), "Did not see description of room in response to look");
        assertTrue(response.contains("magic potion"), "Did not see description of artifacts in response to look");
        assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
    }

    @Test
    void testPartialCommands() throws GameException {
        fetchKeyAndReturnToCabin();

        String response4 = server.handleCommand("Simon: open the trapdoor with the key").toLowerCase();
        assertTrue(response4.contains("you unlock the trapdoor"), "Did not see description of action in response to open");

        String response5 = server.handleCommand("Simon: goto cellar").toLowerCase();
        assertTrue(response5.contains("you travel to the cellar"), "Did not see description of action in response to goto");
    }

    @Test
    void testPartialCommands2() throws GameException {
        fetchKeyAndReturnToCabin();

        String response4 = server.handleCommand("Simon: open").toLowerCase();
        assertTrue(response4.contains("what do you want to interact with?"), "Did not see description of action in response to open");

        String response5 = server.handleCommand("Simon: open trapdoor with key").toLowerCase();
        assertTrue(response5.contains("you unlock the trapdoor and see steps leading down into a cellar"), "Did not see the correct XML narration in response to open");

        String response6 = server.handleCommand("Simon: goto cellar").toLowerCase();
        assertTrue(response6.contains("you travel to the cellar"), "Did not see description of action in response to goto");
    }

    @Test
    void testPartialCommands3() throws GameException {
        fetchKeyAndReturnToCabin();

        String response4 = server.handleCommand("Simon: open with key").toLowerCase();
        assertTrue(response4.contains("what do you want to interact with? you need to mention 'trapdoor' in your command."), "Did not see description of action in response to open");

        String response5 = server.handleCommand("Simon: open trapdoor with key").toLowerCase();
        assertTrue(response5.contains("you unlock the trapdoor and see steps leading down into a cellar"), "Did not see the correct XML narration in response to open");

        String response6 = server.handleCommand("Simon: goto cellar").toLowerCase();
        assertTrue(response6.contains("you travel to the cellar"), "Did not see description of action in response to goto");
    }

    @Test
    void testPartialCommand4() throws GameException {

        String response1 = server.handleCommand("Simon: goto forest").toLowerCase();
        assertTrue(response1.contains("you travel to the forest"), "Did not see description of action in response to goto");

        String response2 = server.handleCommand("Simon: get key").toLowerCase();
        assertTrue(response2.contains("you picked up the key"), "Did not see description of action in response to get");

        String response3 = server.handleCommand("Simon: goto cabin").toLowerCase();
        assertTrue(response3.contains("you travel to the cabin"), "Did not see description of action in response to goto");

        String response4 = server.handleCommand("Simon: open trapdoor").toLowerCase();
        assertTrue(response4.contains("what do you want to interact with? you need to mention 'key' in your command."), "Did not see description of action in response to open");

        String response5 = server.handleCommand("Simon: open trapdoor with key").toLowerCase();
        assertTrue(response5.contains("you unlock the trapdoor and see steps leading down into a cellar"), "Did not see the correct XML narration in response to open");

        String response6 = server.handleCommand("Simon: goto cellar").toLowerCase();
        assertTrue(response6.contains("you travel to the cellar"), "Did not see description of action in response to goto");
    }

    @Test
    void testIntegrationCommands() throws GameException {
        String response1 = server.handleCommand("Simon: look").toLowerCase();
        assertTrue(response1.contains("a log cabin"), "Did not see description of room in response to look");
        assertTrue(response1.contains("magic potion"), "Did not see description of artifacts in response to look");
        assertTrue(response1.contains("wooden trapdoor"), "Did not see description of furniture in response to look");

        String response2 = server.handleCommand("Simon: get axe").toLowerCase();
        assertTrue(response2.contains("you picked up the axe"), "Did not see description of action in response to get");

        String response3 = server.handleCommand("Simon: goto forest").toLowerCase();
        assertTrue(response3.contains("you travel to the forest"), "Did not see description of action in response to goto");

        String response4 = server.handleCommand("Simon: look").toLowerCase();
        assertTrue(response4.contains("dark forest"), "Did not see description of room in response to look");
        assertTrue(response4.contains("key"), "Did not see description of artifacts in response to look");
        assertTrue(response4.contains("cabin"), "Did not see description of room in response to look");

        String response5 = server.handleCommand("Simon: get key").toLowerCase();
        assertTrue(response5.contains("you picked up the key"), "Did not see description of action in response to get");

        String response11 = server.handleCommand("Simon: cut tree").toLowerCase();
        assertTrue(response11.contains("what do you want to interact with? you need to mention 'axe' in your command"), "Did not see description of action in response to cut");

        String response6 = server.handleCommand("Simon: chop tree with axe").toLowerCase();
        assertTrue(response6.contains("you cut down the tree with the axe"), "Did not see description of action in response to chop");

        String response7 = server.handleCommand("Simon: goto cabin").toLowerCase();
        assertTrue(response7.contains("you travel to the cabin"), "Did not see description of action in response to goto");

        String response8 = server.handleCommand("Simon: open the trapdoor with the key").toLowerCase();
        assertTrue(response8.contains("you unlock the trapdoor and see steps leading down into a cellar"), "Did not see description of action in response to open");

        String response9 = server.handleCommand("Simon: goto cellar").toLowerCase();
        assertTrue(response9.contains("you travel to the cellar"), "Did not see description of action in response to goto");

        String response10 = server.handleCommand("Simon: look").toLowerCase();
        assertTrue(response10.contains("dusty cellar"), "Did not see description of room in response to look");
        assertTrue(response10.contains("elf"), "Did not see description of artifacts in response to look");
    }

    private void fetchKeyAndReturnToCabin() throws GameException {
        server.handleCommand("Simon: goto forest");
        server.handleCommand("Simon: get key");
        server.handleCommand("Simon: goto cabin");
    }
}
