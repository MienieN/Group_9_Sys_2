package main.java.zenit.filesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectFileTest {
    private ProjectFile projectFile;
    @BeforeEach
    void setUp() {
       projectFile = spy(new ProjectFile(""));
   }
    @Test
    void addLib ( ) {
        File file;
        file = projectFile.addLib();
        verify(projectFile, times(1)).addLib();
        assertEquals(projectFile.getLib(), file);
    }
    
    @Test
    void getLib ( ) {
        File file = null;
        File lib = projectFile.getLib();
        verify(projectFile, times(1)).getLib();
        assertEquals(lib, file);
    }
    
    @Test
    void setLib ( ) {
        projectFile.setLib(null);
        verify(projectFile, times(1)).setLib(null);
    }
    
    @Test
    void addSrc ( ) {
        File file;
        file = projectFile.addSrc();
        verify(projectFile, times(1)).addSrc();
        assertEquals(projectFile.getSrc(), file);
    }
    
    @Test
    void getSrc ( ) {
        File file = null;
        File src = projectFile.getSrc();
        verify(projectFile, times(1)).getSrc();
        assertEquals(src, file);
    }
    
    @Test
    void addBin ( ) {
        File file;
        file = projectFile.addBin();
        verify(projectFile, times(1)).addBin();
        assertEquals(projectFile.getBin(), file);
    }
    
    @Test
    void getBin ( ) {
        File file = null;
        File bin = projectFile.getBin();
        verify(projectFile, times(1)).getBin();
        assertEquals(bin, file);
    }
    
    @Test
    void addMetadata ( ) {
        File file;
        file = projectFile.addMetadata();
        verify(projectFile, times(1)).addMetadata();
        assertEquals(projectFile.getMetadata(), file);
    }
    
    @Test
    void getMetadata ( ) {
        File file = null;
        File metadata = projectFile.getMetadata();
        verify(projectFile, times(1)).getMetadata();
        assertEquals(metadata, file);
    }
    
    @Test
    void setMetadata ( ) {
        projectFile.setMetadata(null);
        verify(projectFile, times(1)).setMetadata(null);
    }
}