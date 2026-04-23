package edu.uob;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Paths;
import java.io.IOException;
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
      return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
      "Server took too long to respond (probably stuck in an infinite loop)");
  }

  // A lot of tests will probably check the game state using 'look' - so we better make sure 'look' works well !
  @Test
  void testLook() {
      String response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
      assertTrue(response.contains("log cabin"), "Did not see a description of the room in response to look");
      assertTrue(response.contains("magic potion"), "Did not see a description of artifacts in response to look");
      assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
      assertTrue(response.contains("forest"), "Did not see available paths in response to look");
  }

  // Test that we can pick something up and that it appears in our inventory
  @Test
  void testGet() {
      String response;
      sendCommandToServer("simon: get trapdoor");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("trapdoor"), "Trapdoor is a piece of furniture and should not be in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("trapdoor"), "Trapdoor is a piece of furniture and should still be present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get tree");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("tree"), "Tree is a piece of furniture and should not be in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("tree"), "Tree is a piece of furniture and should still be present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get river");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("river"), "River is a piece of furniture and should not be in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("river"), "River is a piece of furniture and should still be present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get ground");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("ground"), "Ground is a piece of furniture and should not be in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("ground"), "Ground is a piece of furniture and should still be present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get hole");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("hole"), "Did not see the hole in the inventory after an attempt was made to get it - hole is a piece of furniture but should be able to be picked up");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("hole"), "Hole is still present in the room after an attempt was made to get it - hole is a piece of furniture but should be able to be picked up");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get path");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("path"), "Path is a piece of furniture and should not be in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("path"), "Path is a piece of furniture and should still be present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get elf");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("elf"), "Elf is a character and should not be in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("elf"), "Elf is a character and should still be present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get lumberjack");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("lumberjack"), "Lumberjack is a character and should not be in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("lumberjack"), "Lumberjack is a character and should still be present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get cabin");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("cabin"), "Cabin is a room and should not be in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("cabin"), "Cabin is still present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get forest");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("forest"), "Forest is a room and should not be in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("forest"), "Forest is still present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get cabin");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("cabin"), "Cabin is a room and should not be in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("cabin"), "Cabin is still present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get riverbank");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("riverbank"), "Riverbank is a room and should not be in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("riverbank"), "Riverbank is still present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get clearing");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("clearing"), "Clearing is a room and should not be in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("clearing"), "Clearing is still present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get get");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("get"), "Did not see the get in the room after an attempt was made to get it - 'get' is a command, not an item");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get look");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("look"), "Did not see the look in the room after an attempt was made to get it - 'look' is a command, not an item");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get drop");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("drop"), "Did not see the drop in the room after an attempt was made to get it - 'drop' is a command, not an item");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get goto");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("goto"), "Did not see the goto in the room after an attempt was made to get it - 'goto' is a command, not an item");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get chop");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("chop"), "Did not see the chop in the room after an attempt was made to get it - 'chop' is a command, not an item");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get dig");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("dig"), "Did not see the dig in the room after an attempt was made to get it - 'dig' is a command, not an item");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get open");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("open"), "Did not see the open in the room after an attempt was made to get it - 'open' is a command, not an item");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get unlock");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("unlock"), "Did not see the unlock in the room after an attempt was made to get it - 'unlock' is a command, not an item");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get cut");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("cut"), "Did not see the cut in the room after an attempt was made to get it - 'cut' is a command, not an item");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get cut down");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("cut down"), "Did not see the cut down in the room after an attempt was made to get it - 'cut down' is a command, not an item");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get drink");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("drink"), "Did not see the drink in the room after an attempt was made to get it - 'drink' is a command, not an item");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get health");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("health"), "Did not see the health in the room after an attempt was made to get it - 'health' is a command, not an item");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get inventory");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("inventory"), "Did not see the inventory in the room after an attempt was made to get it - 'inventory' is a command, not an item");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get inv");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("inv"), "Did not see the inv in the room after an attempt was made to get it - 'inv' is a command, not an item");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get fight");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("fight"), "Did not see the fight in the room after an attempt was made to get it - 'fight' is a command, not an item");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get attack");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("attack"), "Did not see the attack in the room after an attempt was made to get it - 'attack' is a command, not an item");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get hit");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("hit"), "Did not see the hit in the room after an attempt was made to get it - 'hit' is a command, not an item");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get pay");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("pay"), "Did not see the pay in the room after an attempt was made to get it - 'pay' is a command, not an item");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get bridge");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("bridge"), "Did not see the bridge in the room after an attempt was made to get it - 'bridge' is a piece of furniture and should not be able to be picked up");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get blow");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertFalse(response.contains("blow"), "Did not see the blow in the room after an attempt was made to get it - 'blow' is a command, not an item");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
  }

  @Test
  void testValidGetCommands() {
      String response;
      sendCommandToServer("simon: get potion");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Potion is still present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get axe");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("axe"), "Did not see the axe in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("axe"), "Axe is still present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get coin");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("coin"), "Did not see the coin in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("coin"), "Coin is still present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: goto forest");
      response = sendCommandToServer("simon: goto forest");
      response = response.toLowerCase();
      sendCommandToServer("simon: get key");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("key"), "Did not see the key in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("key"), "Key is still present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: goto forest");
      response = sendCommandToServer("simon: goto forest");
      response = response.toLowerCase();
      sendCommandToServer("simon: chop tree");
      response = sendCommandToServer("simon: chop tree");
      response = response.toLowerCase();
      sendCommandToServer("simon: get log");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("log"), "Did not see the log in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("log"), "Log is still present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: goto forest");
      response = sendCommandToServer("simon: goto forest");
      response = response.toLowerCase();
      sendCommandToServer("simon: cut tree");
      response = sendCommandToServer("simon: cut tree");
      response = response.toLowerCase();
      sendCommandToServer("simon: get log");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("log"), "Did not see the log in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("log"), "Log is still present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: goto forest");
      response = sendCommandToServer("simon: goto forest");
      response = response.toLowerCase();
      sendCommandToServer("simon: cut down tree");
      response = sendCommandToServer("simon: cut down tree");
      response = response.toLowerCase();
      sendCommandToServer("simon: get log");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("log"), "Did not see the log in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("log"), "Log is still present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: goto forest");
      response = sendCommandToServer("simon: goto forest");
      response = response.toLowerCase();
      sendCommandToServer("simon: get key");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      sendCommandToServer("simon: goto cabin");
      response = sendCommandToServer("simon: goto cabin");
      response = response.toLowerCase();
      sendCommandToServer("simon: get coin");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      sendCommandToServer("simon: open trapdoor");
      response = sendCommandToServer("simon: open trapdoor");
      response = response.toLowerCase();
      sendCommandToServer("simon: goto cellar");
      response = sendCommandToServer("simon: goto cellar");
      response = response.toLowerCase();
      sendCommandToServer("simon: pay elf");
      response = sendCommandToServer("simon: pay elf");
      response = response.toLowerCase();
      sendCommandToServer("simon: get shovel");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("shovel"), "Did not see the shovel in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("shovel"), "Shovel is still present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get horn");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("horn"), "Did not see the horn in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("horn"), "Horn is still present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();

      sendCommandToServer("simon: get gold");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("gold"), "Did not see the gold in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("gold"), "Gold is still present in the room after an attempt was made to get it");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
  }

  // Test that we can goto a different location (we won't get very far if we can't move around the game !)
  @Test
  void testGoto()
  {
      sendCommandToServer("simon: goto forest");
      String response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("key"), "Failed attempt to use 'goto' command to move to the forest - there is no key in the current location");
  }

  @Test
  void testDrop()
  {
      sendCommandToServer("simon: drop");
      String response = sendCommandToServer("simon: look");
      response = response.toLowerCase();

  }

  // Add more unit tests or integration tests here.

}
