package zenit.terminal;
import static org.junit.jupiter.api.Assertions.*;
import com.kodedu.terminalfx.config.TerminalConfig;
import main.java.zenit.terminal.TerminalController;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApplicationExtension.class)
class TerminalControllerTest {

    private main.java.zenit.terminal.TerminalController controller;
    private TabPane tabPane;
    private AnchorPane basePane;

    @Start
    private void start(Stage stage) {
        tabPane = new TabPane();
        basePane = new AnchorPane();
        controller = new main.java.zenit.terminal.TerminalController();
        controller.tabPane = tabPane;
        controller.basePane = basePane;
        controller.initialize();
        stage.setScene(new Scene(basePane, 800, 600));
        stage.show();
    }

    @Test
    void testInitialize_AddsTerminalTab() {
        assertEquals(1, tabPane.getTabs().size(), "Setup terminaltab");
    }

    @Test
    void testDarkModeConfig() {
        TerminalConfig darkConfig = new TerminalController().getDarkTerminalConfig();
        
        assertEquals("#000000", darkConfig.getBackgroundColor(), "black background");
        assertEquals("#FFFFFF", darkConfig.getForegroundColor(), "white foreground");
        assertTrue(darkConfig.isCursorBlink(), "Check Blink true.");
        assertEquals("#FFFFFF", darkConfig.getCursorColor(), "white curser");
        assertEquals("consolas", darkConfig.getFontFamily(), "check font");
    }
}
