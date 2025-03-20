package zenit.ui;

import static org.junit.jupiter.api.Assertions.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import main.java.zenit.ui.TestUI;

@ExtendWith(ApplicationExtension.class)
class TestUITest {

    private TestUI testUI;

    @Start
    private void start(Stage stage) {
        testUI = new TestUI();
        testUI.start(stage);
    }

    @Test
    void testApplicationStarts() {
        assertNotNull(testUI, "should start the app");
    }
}
