package main.java.zenit.filesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkspaceHandlerTest {
    private WorkspaceHandler workspaceHandler;
    
    @BeforeEach
    void setUp ( ) {
        workspaceHandler = spy(new WorkspaceHandler());
    }
    
    @Test
    void createWorkspace ( ) {
        boolean success = workspaceHandler.createWorkspace(null);
        verify(workspaceHandler, times(1)).createWorkspace(null);
        assertEquals(success, true);
    }
    
    @Test
    void setUpNewWorkspace ( ) {
        File file = workspaceHandler.setUpNewWorkspace();
        assertNotNull(file);
    }
}