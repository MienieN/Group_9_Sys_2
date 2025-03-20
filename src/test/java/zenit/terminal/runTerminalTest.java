package zenit.terminal;
import javafx.stage.Stage;
import main.java.zenit.terminal.runTerminal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class runTerminalTest {

    private runTerminal terminal;
    private Stage testStage;

    @Start
    private void start(Stage stage) {
        terminal = new runTerminal();
        testStage = stage;
        terminal.start(stage);
    }

    @Test
    void testStageTitleIsSetCorrectly() {
        assertEquals("Zenit", testStage.getTitle(), "title set to zenit");
    }

    @Test
    void testSceneIsNotNull() {
        assertNotNull(testStage.getScene(), "check if stage has a scene and is not null");
    }
}
