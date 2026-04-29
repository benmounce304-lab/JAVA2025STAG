package edu.uob;

import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.nio.file.Paths;

public class CMDTestExt {
    private GameServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

}
