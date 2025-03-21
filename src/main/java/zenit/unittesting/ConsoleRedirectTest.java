package main.java.zenit.unittesting;

import main.java.zenit.ConsoleRedirect;
import main.java.zenit.console.ConsoleArea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConsoleRedirectTest {
    private ConsoleArea consoleArea;

    @BeforeEach
    public void setUp() {
        consoleArea = new ConsoleArea();
    }

    @Test
    public void testConsoleRedirect_shouldRedirectSystemOut() {
        ConsoleRedirect consoleRedirect = new ConsoleRedirect(consoleArea);


        System.out.println("Hello World");


        assertNotNull(consoleArea.getText());
        assertTrue(consoleArea.getText().contains("Hello World"));
    }

    @Test
    public void testConsoleRedirect_shouldRedirectSystemErr() {
        ConsoleRedirect consoleRedirect = new ConsoleRedirect(consoleArea);


        System.err.println("Error message");


        assertNotNull(consoleArea.getText());
        assertTrue(consoleArea.getText().contains("Error message"));
    }
}
