package main.java.zenit.filesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RunnableClassTest {
    private RunnableClass runnableClass;
    
    @BeforeEach
    void setUp ( ) {
        runnableClass = spy(new RunnableClass(""));
    }
    @Test
    void getPath ( ) {
        String path = "";
        String path1 = runnableClass.getPath();
        
        verify(runnableClass, times(1)).getPath();
        assertEquals(path1, path);
    }
    
    @Test
    void setPath ( ) {
        String path = "";
        runnableClass.setPath(path);
        verify(runnableClass, times(1)).setPath(path);
    }
    
    @Test
    void getPaArguments ( ) {
        String paArguments = "";
        String paArguments1 = runnableClass.getPaArguments();
        verify(runnableClass, times(1)).getPaArguments();
        assertEquals(paArguments1, paArguments);
    }
    
    @Test
    void setPaArguments ( ) {
        String paArguments = "";
        runnableClass.setPaArguments(paArguments);
        verify(runnableClass, times(1)).setPaArguments(paArguments);
    }
    
    @Test
    void getVmArguments ( ) {
        String vmArguments = "";
        String vmArguments1 = runnableClass.getVmArguments();
        verify(runnableClass, times(1)).getVmArguments();
        assertEquals(vmArguments1, vmArguments);
    }
    
    @Test
    void setVmArguments ( ) {
        String vmArguments = "";
        runnableClass.setVmArguments(vmArguments);
        verify(runnableClass, times(1)).setVmArguments(vmArguments);
    }
    
    @Test
    void testToString ( ) {
        // will not test as toString methods are difficult to test due to inconsistent results.
    }
}