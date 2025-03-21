package main.java.zenit.settingspanel;

import static org.junit.jupiter.api.Assertions.*;
import javafx.application.Platform;
import javafx.stage.Stage;
import main.java.zenit.Zenit;
import main.java.zenit.ui.MainController;
import main.java.zenit.settingspanel.SettingsPanelController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class SettingsPanelControllerTest extends ApplicationTest {

    private static Zenit zenit;
    private MainController mainController;
    private SettingsPanelController settingsPanelController;

    @Override
    public void start(Stage stage) throws Exception {
        zenit = new Zenit();
        zenit.start(stage); // Start JavaFX properly
    }

    @BeforeEach
    void setUp() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            mainController = zenit.getMainController();
            settingsPanelController = mainController.getSettingsPanel();
            latch.countDown();
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS), "JavaFX thread did not finish in time.");
        assertNotNull(mainController, "MainController should not be null after JavaFX starts.");
        assertNotNull(settingsPanelController, "SettingsPanelController should not be null.");
    }

    @Test
    void changeFontSize() {
        Platform.runLater(() -> {
            long size = settingsPanelController.setNewFontSize(99);
            assertEquals(99, size);
        });
    }

    @Test
    void changeFontSizeTooLarge() {
        Platform.runLater(() -> {
            long size = settingsPanelController.setNewFontSize(200);
            assertEquals(100, size);
        });
    }

    @Test
    void changeFontSizeTooSmall() {
        Platform.runLater(() -> {
            long size = settingsPanelController.setNewFontSize(5);
            assertEquals(6, size);
        });
    }
}