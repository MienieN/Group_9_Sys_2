package main.java.zenit;

import static org.mockito.Mockito.*;

import java.io.File;
import javafx.application.Platform;
import javafx.stage.Stage;
import main.java.zenit.launchers.MacOSLauncher;
import main.java.zenit.setup.SetupController;
import main.java.zenit.ui.MainController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
class ZenitTest {

    @Mock
    private SetupController setupController;

    @Mock
    private MacOSLauncher macOSLauncher;

    @Mock
    private MainController mainController;

    @Mock
    private Stage stage;

    @Mock
    private File workspaceFile;

    @Mock
    private File jdkFile;

    @Mock
    private File defaultJdkFile;

    private Zenit zenit;

    @BeforeEach
    void setUp() {
        zenit = spy(new Zenit());

        workspaceFile = mock(File.class);
        jdkFile = mock(File.class);
        defaultJdkFile = mock(File.class);

    }

    //Might need a refactor in this class again to be able to test all of it.



    /*
    @Test
    void testStartNoFiles() throws Exception {
        when(workspaceFile.exists()).thenReturn(false);
        when(jdkFile.exists()).thenReturn(false);
        when(defaultJdkFile.exists()).thenReturn(false);

        try {
            zenit.start(stage);
            verify(setupController, times(1)).start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

     */



    @Test
    void testStartExistingFiles() throws Exception {
        Platform.runLater(() -> {
            try {
                zenit.start(stage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            verifyNoInteractions(setupController);
        });
    }

    @Test
    void testStop() throws Exception{
        Platform.runLater(() -> {
            try {
                zenit.start(stage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            zenit.stop();
            verify(zenit,times(1)).stop();
        });

    }


}
