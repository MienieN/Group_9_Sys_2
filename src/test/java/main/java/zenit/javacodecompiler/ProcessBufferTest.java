package main.java.zenit.javacodecompiler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
class ProcessBufferTest {
    private ProcessBuffer processBuffer;

    @BeforeEach
    void setup(){
        processBuffer = new ProcessBuffer();
    }

    @Test
    void testIsempty(){
        assertTrue(processBuffer.isEmpty());
    }

    @Test
    void testPut() throws IOException {
        Process mockprocess = new ProcessBuilder("cmd", "/c", "echo", "test").start(); //this is for windows and our tests runs on "windows-latest"
        processBuffer.put(mockprocess);
        assertFalse(processBuffer.isEmpty());
    }

    @Test
    void testGet() throws IOException{
        Process mockprocess = new ProcessBuilder("cmd", "/c", "echo", "test").start(); //this is for windows and our tests runs on "windows-latest"
        processBuffer.put(mockprocess);
        assertAll("Check to see if the process matches with get and that the buffer is empty after running the get()",
                () -> assertEquals(mockprocess, processBuffer.get(),"Should match"),
                () -> assertTrue(processBuffer.isEmpty()));
    }

}