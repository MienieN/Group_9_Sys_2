package main.java.zenit.unittesting;

import main.java.zenit.ui.Launcher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class LauncherTest {
    @Test
    public void testMainMethod() {
        assertDoesNotThrow(() -> Launcher.main(new String[0]));
    }

}
