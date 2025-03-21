package main.java.zenit.unittesting;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import main.java.zenit.ui.KeyboardShortcuts;
import main.java.zenit.ui.MainController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KeyboardShortcutsTest {
    private Scene scene;
    private MainController controller;


    @BeforeEach
    public void setUp() {
        scene = new Scene(new Group(), 800, 600);
        controller = new MainController(new Stage());
    }


    @Test
    public void testAdd() {
        Runnable action = () -> {};
        KeyboardShortcuts.add(scene, KeyCode.SPACE, KeyCombination.CONTROL_DOWN, action);
        assertTrue(scene.getAccelerators().containsKey(new KeyCodeCombination(KeyCode.SPACE, KeyCombination.CONTROL_DOWN)));
    }


    @Test
    public void testSetupMain() {
        KeyboardShortcuts.setupMain(scene, controller);
        assertNotNull(scene.getOnKeyPressed());
    }


    // this class is not finished yet
}
