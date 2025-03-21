/*
package zenit.ui.tree;
import javafx.application.Platform;
import javafx.stage.Stage;
import main.java.zenit.ui.MainController;
import main.java.zenit.ui.tree.InsertMenu;
import main.java.zenit.zencodearea.ZenCodeArea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InsertMenuTest extends ApplicationTest {
    @Mock
    private MainController mainController;
    @Mock
    private ZenCodeArea codeArea;
    private InsertMenu insertMenu;
    
    @Override
    public void start(Stage stage) {
        MockitoAnnotations.openMocks(this);
        insertMenu = spy(new InsertMenu(mainController, codeArea));
        
    }
//    @BeforeEach
//    void setUp() {
//        insertMenu = spy(new InsertMenu(mainController, codeArea));
//    }
    
    @Test
    void testInsertMenu() {
        Platform.runLater(() -> {;
            insertMenu.setup();
            verify(insertMenu, times(1)).setup();
        });
        
    }
}*/
