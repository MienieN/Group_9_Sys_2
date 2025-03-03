package main.java.zenit.console;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConsoleAreaTest {
    private ConsoleArea consoleArea;

    @BeforeEach
    void setup() {
        consoleArea = new ConsoleArea("TestID", null, "blue");
    }

    @Test
    void testSetAndGetFileName() {
        consoleArea.setFileName("testFile.txt");
        assertEquals("testFile.txt", consoleArea.getFileName());
    }

    @Test
    void testSetAndGetBackgroundColor() {
        consoleArea.setBackgroundColor("red");
        assertEquals("red", consoleArea.getBackgroundColor());
    }

    @Test
    void testSetAndGetProcess() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        Process mockprocess;
        if (os.contains("mac os")) {
            mockprocess = new ProcessBuilder("echo", "test").start(); // this is for macOS.
        } else {
            mockprocess = new ProcessBuilder().command("cmd.exe", "/c", "echo", "test").start(); // This command is specific to Windows as I'm running the tests on Windows.
        }
        consoleArea.setProcess(mockprocess);
        assertNotNull(consoleArea.getProcess());
        assertEquals(mockprocess, consoleArea.getProcess());
    }

    @Test
    void testSetAndGetID() {
        consoleArea.setID("NewID");
        assertEquals("NewID", consoleArea.getID());
    }

    @Test
    void testToString() {
        assertEquals("TestID", consoleArea.toString());
    }

}
