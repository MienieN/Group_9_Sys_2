package main.java.zenit.filesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileControllerTest {
    private FileController fileController;
    
    @BeforeEach
    void setUp ( ) {
        fileController = spy(new FileController());
    }
    
    @Test
    void createFileAtLocationWithContentAndType ( ) {
        File file = fileController.getWorkspace();
        File file1 = fileController.createFileAtLocationWithContentAndType(file, "Test", 0);
        verify(fileController, times(1)).createFileAtLocationWithContentAndType(file,
                "Test",  0);
        assertEquals(file, file1);
    }
    
    @Test
    void createFileWithType ( ) {
        File file = fileController.getWorkspace();
        File file1 = fileController.createFileWithType(file, 0);
        verify(fileController, times(1)).createFileWithType(file, 0);
        assertEquals(file, file1);
    }
    
    @Test
    void createFileWithContentAndEmptyType ( ) {
        File file = fileController.getWorkspace();
        File file1 = fileController.createFileWithContentAndEmptyType(file, "Test");
        verify(fileController, times(1)).createFileWithContentAndEmptyType(file, "Test");
        assertEquals(file, file1);
    }
    
    @Test
    void readJavaFileHandlerFileContent ( ) {
        File file = fileController.getWorkspace();
        String string = fileController.readJavaFileHandlerFileContent(file);
        verify(fileController, times(1)).readJavaFileHandlerFileContent(file);
        assertEquals(null, string);
    }
    
    @Test
    void readFile ( ) {
        File file = null;
        String string = fileController.readFile(file);
        assertEquals("", string);
    }
    
    @Test
    void writeContentToFile ( ) {
        boolean test = fileController.writeContentToFile(null, "Test");
        verify(fileController, times(1)).writeContentToFile(null, "Test");
        assertFalse(test);
    }
    
    @Test
    void renameFile ( ) {
        File file = fileController.renameFile(null, "Test");
        verify(fileController, times(1)).renameFile(null, "Test");
        assertNull(file);
    }
    
    @Test
    void deleteFile ( ) {
        boolean test = fileController.deleteFile(null);
        verify(fileController, times(1)).deleteFile(null);
        assertFalse(test);
    }
    
    @Test
    void createProject ( ) {
        File file = fileController.createProject("Test");
        verify(fileController, times(1)).createProject("Test");
        assertNull(file);
    }
    
    @Test
    void changeWorkspace ( ) {
        boolean test = fileController.changeWorkspace(fileController.getWorkspace());
        verify(fileController, times(1)).changeWorkspace(fileController.getWorkspace());
        assertTrue(test);
    }
    
    @Test
    void getWorkspace ( ) {
        File file = fileController.getWorkspace();
        verify(fileController, times(1)).getWorkspace();
        assertNull(file);
    }
    
    @Test
    void checkIfClassFileContainsMainMethod ( ) {
        boolean test = fileController.checkIfClassFileContainsMainMethod(new File("Test"));
        verify(fileController, times(1)).checkIfClassFileContainsMainMethod(new File("Test"));
        assertFalse(test);
    }
    
}