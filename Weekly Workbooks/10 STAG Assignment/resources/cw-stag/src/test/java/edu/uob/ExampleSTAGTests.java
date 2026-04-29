package edu.uob;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

class ExampleSTAGTests {

    private GameServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
                    return server.handleCommand(command);
                },
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    // A lot of tests will probably check the game state using 'look' - so we better make sure 'look' works well !
    @Test
    void testLookStaticState() {
        String response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
        assertTrue(response.contains("log cabin"), "Did not see a description of the room in response to look");

        assertTrue(response.contains("magic potion"), "Did not see a description of artifacts in response to look");
        assertTrue(response.contains("axe"), "Did not see available items in response to look");
        assertTrue(response.contains("razor sharp axe"), "Did not see description of artefacts in response to look");
        assertTrue(response.contains("coin"), "Did not see available items in response to look");
        assertTrue(response.contains("silver coin"), "Did not see description of artefacts in response to look");

        assertTrue(response.contains("trapdoor"), "Did not see description of furniture in response to look");
        assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");

        assertTrue(response.contains("forest"), "Did not see available paths in response to look");

        assertFalse(response.contains("key"), "The key should not be visible in the cabin");
        assertFalse(response.contains("rusty old key"), "The rusty old key should not be visible in the cabin");

        assertFalse(response.contains("elf"), "The elf should not be an available characters in the cabin");
        assertFalse(response.contains("angry looking elf"), "The angry looking elf should not be an available characters in the cabin");
    }

    @Test
    void testLookDynamicState() {
        String look1 = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(look1.contains("potion"), "Potion should be visible initially");

        sendCommandToServer("simon: get potion");
        String look2 = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(look2.contains("potion"), "Potion should disappear from room after being picked up");

        sendCommandToServer("simon: drop potion");
        String look3 = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(look3.contains("potion"), "Potion should reappear in room after being dropped");

        String look4 = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(look4.contains("axe"), "Axe should be visible initially");

        sendCommandToServer("simon: get axe");
        String look6 = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(look6.contains("axe"), "Axe should disappear from room after being picked up");

        sendCommandToServer("simon: drop axe");
        String look5 = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(look5.contains("axe"), "Axe should reappear in room after being dropped");

        String look7 = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(look7.contains("coin"), "Coin should be visible initially");

        sendCommandToServer("simon: get coin");
        String look8 = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(look8.contains("coin"), "Coin should disappear from room after being picked up");

        sendCommandToServer("simon: drop coin");
        String look9 = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(look9.contains("coin"), "Coin should reappear in room after being dropped");

        String look10 = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(look10.contains("trapdoor"), "Trapdoor should be visible initially");
    }

    @Test
    void testLookMultiplayerState() {
        String simonLook1 = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(simonLook1.contains("simon"), "Simon should not see himself in the room when he looks");
        assertFalse(simonLook1.contains("ben"), "Ben has not joined yet");

        String benLook1 = sendCommandToServer("ben: look").toLowerCase();
        assertTrue(benLook1.contains("simon"), "Ben should see Simon in the room when he looks");
        assertFalse(benLook1.contains("ben"), "Ben should not see himself");

        String simonLook2 = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(simonLook2.contains("ben"), "Simon should see Ben in the room when he looks");
    }

    @Test
    void testLookNegativeSpace() {
        String forestLook1 = sendCommandToServer("simon: goto forest");
        forestLook1 = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(forestLook1.contains("cabin"), "Did not see the cabin in response to look");
        assertFalse(forestLook1.contains("log cabin"), "Did not see the description of the cabin in response to look");

        assertFalse(forestLook1.contains("potion"), "The potion should not be visible from the forest");
        assertFalse(forestLook1.contains("magic potion"), "The magic potion should not be visible from the forest");
        assertFalse(forestLook1.contains("axe"), "The axe should not be visible from the forest");
        assertFalse(forestLook1.contains("razor sharp axe"), "The razor sharp axe should not be visible from the forest");
        assertFalse(forestLook1.contains("coin"), "The coin should not be visible from the forest");
        assertFalse(forestLook1.contains("silver coin"), "The silver coin should not be visible from the forest");

        assertFalse(forestLook1.contains("trapdoor"), "The trapdoor should not be visible from the forest");
        assertFalse(forestLook1.contains("wooden trapdoor"), "The wooden trapdoor should not be visible from the forest");

        assertTrue(forestLook1.contains("forest"), "Did not see the name of the current room in response to look");

        assertTrue(forestLook1.contains("key"), "Did not see available items in response to look");
        assertTrue(forestLook1.contains("rusty old key"), "Did not see the description of the rusty old key in response to look");

        assertFalse(forestLook1.contains("elf"), "The elf should not be an available characters in the forest");
        assertFalse(forestLook1.contains("angry looking elf"), "The angry looking elf should not be an available characters in the forest");

        assertFalse(forestLook1.contains("horn"), "The horn should not be visible from the forest");

    }

    // Test that we can pick something up and that it appears in our inventory
    @Test
    void testValidGet() {
        sendCommandToServer("simon: get potion");
        String inv1 = sendCommandToServer("simon: inv").toLowerCase();
        assertTrue(inv1.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
        String look1 = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(look1.contains("potion"), "Potion is still present in the room after an attempt was made to get it");
        String inv2 = sendCommandToServer("simon: inv").toLowerCase();

        sendCommandToServer("simon: get axe");
        String inv3 = sendCommandToServer("simon: inv").toLowerCase();
        assertTrue(inv3.contains("axe"), "Did not see the axe in the inventory after an attempt was made to get it");
        String look2 = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(look2.contains("axe"), "Axe is still present in the room after an attempt was made to get it");
        String inv4 = sendCommandToServer("simon: inv").toLowerCase();


        sendCommandToServer("simon: get coin");
        String inv5 = sendCommandToServer("simon: inv").toLowerCase();
        assertTrue(inv5.contains("coin"), "Did not see the coin in the inventory after an attempt was made to get it");
        String look3 = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(look3.contains("coin"), "Coin is still present in the room after an attempt was made to get it");
        String inv6 = sendCommandToServer("simon: inv").toLowerCase();

        sendCommandToServer("simon: goto forest");
        String goto1 = sendCommandToServer("simon: goto forest").toLowerCase();
        sendCommandToServer("simon: get key");
        String inv7 = sendCommandToServer("simon: inv").toLowerCase();
        assertTrue(inv7.contains("key"), "Did not see the key in the inventory after an attempt was made to get it");
        String look4 = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(look4.contains("key"), "Key is still present in the room after an attempt was made to get it");
        String inv8 = sendCommandToServer("simon: inv").toLowerCase();

        sendCommandToServer("simon: goto forest");
        goto1 = sendCommandToServer("simon: goto forest").toLowerCase();
        sendCommandToServer("simon: chop tree");
        String chop1 = sendCommandToServer("simon: chop tree").toLowerCase();
        sendCommandToServer("simon: get log");
        String inv9 = sendCommandToServer("simon: inv").toLowerCase();
        assertTrue(inv9.contains("log"), "Did not see the log in the inventory after an attempt was made to get it");
        String look5 = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(look5.contains("log"), "Log is still present in the room after an attempt was made to get it");
        String inv10 = sendCommandToServer("simon: inv").toLowerCase();

        sendCommandToServer("simon: goto forest");
        String goto2 = sendCommandToServer("simon: goto forest").toLowerCase();
        sendCommandToServer("simon: cut tree");
        String chop2 = sendCommandToServer("simon: cut tree").toLowerCase();
        sendCommandToServer("simon: get log");
        String inv11 = sendCommandToServer("simon: inv").toLowerCase();
        assertTrue(inv11.contains("log"), "Did not see the log in the inventory after an attempt was made to get it");
        String look6 = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(look6.contains("log"), "Log is still present in the room after an attempt was made to get it");
        String inv12 = sendCommandToServer("simon: inv").toLowerCase();

        sendCommandToServer("simon: goto forest");
        String goto3 = sendCommandToServer("simon: goto forest").toLowerCase();
        sendCommandToServer("simon: cut down tree");
        String chop3 = sendCommandToServer("simon: cut down tree").toLowerCase();
        sendCommandToServer("simon: get log");
        String inv13 = sendCommandToServer("simon: inv").toLowerCase();
        assertTrue(inv13.contains("log"), "Did not see the log in the inventory after an attempt was made to get it");
        String look7 = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(look7.contains("log"), "Log is still present in the room after an attempt was made to get it");
        String inv14 = sendCommandToServer("simon: inv").toLowerCase();

        sendCommandToServer("simon: goto forest");
        String goto4 = sendCommandToServer("simon: goto forest").toLowerCase();
        sendCommandToServer("simon: get key");
        String inv15 = sendCommandToServer("simon: inv").toLowerCase();
        sendCommandToServer("simon: goto cabin");
        String goto5 = sendCommandToServer("simon: goto cabin").toLowerCase();
        sendCommandToServer("simon: get coin");
        String inv16 = sendCommandToServer("simon: inv").toLowerCase();
        sendCommandToServer("simon: open trapdoor");
        String openTrapdoor1 = sendCommandToServer("simon: open trapdoor").toLowerCase();
        sendCommandToServer("simon: goto cellar");
        String goto6 = sendCommandToServer("simon: goto cellar").toLowerCase();
        sendCommandToServer("simon: pay elf");
        String payElf1 = sendCommandToServer("simon: pay elf").toLowerCase();
        sendCommandToServer("simon: get shovel");
        String inv17 = sendCommandToServer("simon: inv").toLowerCase();
        assertTrue(inv17.contains("shovel"), "Did not see the shovel in the inventory after an attempt was made to get it");
        String look8 = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(look8.contains("shovel"), "Shovel is still present in the room after an attempt was made to get it");
        String inv18 = sendCommandToServer("simon: inv").toLowerCase();

        sendCommandToServer("simon: goto forest");
        String goto7 = sendCommandToServer("simon: goto forest").toLowerCase();
        sendCommandToServer("simon: get key");
        String inv19 = sendCommandToServer("simon: inv").toLowerCase();
        sendCommandToServer("simon: goto cabin");
        String goto8 = sendCommandToServer("simon: goto cabin").toLowerCase();
        sendCommandToServer("simon: get coin");
        String inv20 = sendCommandToServer("simon: inv").toLowerCase();
        sendCommandToServer("simon: open trapdoor");
        String open1 = sendCommandToServer("simon: open trapdoor").toLowerCase();
        sendCommandToServer("simon: goto cellar");
        String goto9 = sendCommandToServer("simon: goto cellar").toLowerCase();
        sendCommandToServer("simon: pay elf");
        String pay1 = sendCommandToServer("simon: pay elf").toLowerCase();
        sendCommandToServer("simon: get shovel");
        String inv21 = sendCommandToServer("simon: inv").toLowerCase();
        sendCommandToServer("simon: goto cabin");
        String goto10 = sendCommandToServer("simon: goto cabin").toLowerCase();
        sendCommandToServer("simon: goto forest");
        String goto11 = sendCommandToServer("simon: goto forest").toLowerCase();
        sendCommandToServer("simon: goto riverbank");
        String goto12 = sendCommandToServer("simon: goto riverbank").toLowerCase();
        sendCommandToServer("simon: get horn");
        String inv22 = sendCommandToServer("simon: inv").toLowerCase();
        assertTrue(inv22.contains("horn"), "Did not see the horn in the inventory after an attempt was made to get it");
        String look9 = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(look9.contains("horn"), "Horn is still present in the room after an attempt was made to get it");
        String inv23 = sendCommandToServer("simon: inv").toLowerCase();

        sendCommandToServer("simon: goto forest");
        String goto13 = sendCommandToServer("simon: goto forest").toLowerCase();
        sendCommandToServer("simon: get key");
        String inv24 = sendCommandToServer("simon: inv").toLowerCase();
        sendCommandToServer("simon: goto cabin");
        String goto14 = sendCommandToServer("simon: goto cabin").toLowerCase();
        sendCommandToServer("simon: get coin");
        String inv25 = sendCommandToServer("simon: inv").toLowerCase();
        sendCommandToServer("simon: open trapdoor");
        String open2 = sendCommandToServer("simon: open trapdoor").toLowerCase();
        sendCommandToServer("simon: goto cellar");
        String goto15 = sendCommandToServer("simon: goto cellar").toLowerCase();
        sendCommandToServer("simon: pay elf");
        String pay2 = sendCommandToServer("simon: pay elf").toLowerCase();
        sendCommandToServer("simon: get shovel");
        String inv26 = sendCommandToServer("simon: inv").toLowerCase();
        sendCommandToServer("simon: goto cabin");
        String goto16 = sendCommandToServer("simon: goto cabin").toLowerCase();
        sendCommandToServer("simon: goto forest");
        String goto17 = sendCommandToServer("simon: goto forest").toLowerCase();
        sendCommandToServer("simon: cut tree");
        String cutTree2 = sendCommandToServer("simon: cut tree").toLowerCase();
        sendCommandToServer("simon: get log");
        String inv27 = sendCommandToServer("simon: inv").toLowerCase();
        sendCommandToServer("simon: goto riverbank");
        String goto18 = sendCommandToServer("simon: goto riverbank").toLowerCase();
        sendCommandToServer("simon: bridge river");
        String bridge2 = sendCommandToServer("simon: bridge river").toLowerCase();
        sendCommandToServer("simon: goto clearing");
        String goto19 = sendCommandToServer("simon: goto clearing").toLowerCase();
        sendCommandToServer("simon: dig hole");
        String dig2 = sendCommandToServer("simon: dig hole").toLowerCase();
        sendCommandToServer("simon: get gold");
        String inv28 = sendCommandToServer("simon: inv").toLowerCase();
        assertTrue(inv28.contains("gold"), "Did not see the gold in the inventory after an attempt was made to get it");
        String look10 = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(look10.contains("gold"), "Gold is still present in the room after an attempt was made to get it");
        String inv29 = sendCommandToServer("simon: inv").toLowerCase();
    }

    @Test
    void testInvalidGet() {
        sendCommandToServer("simon: get trapdoor");
        String inv1 = sendCommandToServer("simon: inv").toLowerCase();
        assertFalse(inv1.contains("trapdoor"), "Trapdoor is a piece of furniture and should not be in the inventory after an attempt was made to get it");
        String look1 = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(look1.contains("trapdoor"), "Trapdoor is a piece of furniture and should still be present in the room after an attempt was made to get it");

        sendCommandToServer("simon: get cabin");
        String inv2 = sendCommandToServer("simon: inv").toLowerCase();
        assertFalse(inv2.contains("cabin"), "Cabin is a room and should not be in the inventory after an attempt was made to get it");
        String look2 = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(look2.toLowerCase().contains("cabin"), "Cabin is a room and should still be present in the room after an attempt was made to get it");

        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get elf").toLowerCase();
        String inv3 = sendCommandToServer("simon: inv").toLowerCase();
        assertFalse(inv3.contains("elf"), "Elf is a creature and should not be in the inventory after an attempt was made to get it");
    }

    @Test
    void testExtraneousEntities() {
        String response;
        sendCommandToServer("simon: get key");
        response = sendCommandToServer("simon: inv").toLowerCase();
        response = response.toLowerCase();
        assertFalse(response.contains("key"), "Key should not be visible in the cabin");

        sendCommandToServer("simon: get phone");
        response = sendCommandToServer("simon: inv").toLowerCase();
        response = response.toLowerCase();
        assertFalse(response.contains("phone"), "This is not an entity in the game, so it should not be in the inventory");
    }

    // Test that we can goto a different location (we won't get very far if we can't move around the game !)
    @Test
    void testValidGoto() {
        sendCommandToServer("simon: goto forest");
        String goto1 = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(goto1.contains("forest"), "Did not see the forest in response to goto");

        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: goto cabin");
        String goto2 = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(goto2.contains("cabin"), "Did not see the cabin in response to goto");

        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");

        sendCommandToServer("simon: open trapdoor");
        String goto3 = sendCommandToServer("simon: goto cellar").toLowerCase();
        assertTrue(goto3.contains("cellar"), "Did not see the cellar in response to goto");
    }

    @Test
    void testInvalidGoto() {
        sendCommandToServer("simon: goto invalid location");
        String response = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(response.contains("invalid location"), "Invalid location should not be visible in the room");


    }

    @Test
    void testInvalidDrop() {
        sendCommandToServer("simon: drop potion");
        String response = sendCommandToServer("simon: inv").toLowerCase();
        assertFalse(response.contains("potion"), "Potion should not be in the inventory after attempting to drop it");
    }

    @Test
    void testInvalidCommandWithSpace() {
        String response = sendCommandToServer("simon invalid command");
        assertTrue(response.contains("Invalid command"), "Invalid command should have been rejected");
    }

    @Test
    void testInvalidCommandWithMultipleSpaces() {
        String response = sendCommandToServer("simon   invalid command");
        assertTrue(response.contains("Invalid command"), "Invalid command should have been rejected");
    }

    @Test
    void testInvalidCommandWithExtraSpaces() {
        String response = sendCommandToServer("simon   invalid command   ");
        assertTrue(response.contains("Invalid command"), "Invalid command should have been rejected");
    }

    // Add more unit tests or integration tests here.

}
