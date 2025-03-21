package main.java.zenit.unittesting;

import main.java.zenit.ui.NewFolderController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NewFolderControllerTest {
    private NewFolderController controller;
    private File workspace;


    @BeforeEach
    public void setUp() {
        workspace = new File(System.getProperty("user.dir"));
        controller = new NewFolderController(workspace, true);
    }


    @Test
    public void testFilePathInitialization() {
        controller.start();
        assertEquals(workspace.getPath(), controller.filePath.getItems().get(0));
    }


    @Test
    public void testDarkModeStylesheet() {
        controller.ifDarkModeChanged(true);
        assertTrue(controller.stage.getScene().getStylesheets().contains("/zenit/ui/projectinfo/mainStyle.css"));
    }


    @Test
    public void testLightModeStylesheet() {
        controller.ifDarkModeChanged(false);
        assertTrue(controller.stage.getScene().getStylesheets().contains("/zenit/ui/projectinfo/mainStyle-lm.css"));
    }

}
