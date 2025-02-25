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
    void testIsEmptyAtStart(){
        assertTrue(processBuffer.isEmpty());
    }

    @Test
    void testPut() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        Process mockprocess;
        if (os.contains("mac os")){
             mockprocess = new ProcessBuilder("echo", "test").start(); //this is for macOS and our tests runs on "windows-latest"
        }
        else {
             mockprocess = new ProcessBuilder("cmd", "/c", "echo", "test").start(); //this is for windows and our tests runs on "windows-latest"
        }
        processBuffer.put(mockprocess);
        assertFalse(processBuffer.isEmpty());
    }


    @Test
    void testGet() throws IOException{
        String os = System.getProperty("os.name").toLowerCase();
        Process mockprocess;
        if (os.contains("mac os")){
             mockprocess = new ProcessBuilder("echo", "test").start(); //this is for macOS and our tests runs on "windows-latest"
        }
        else {
             mockprocess = new ProcessBuilder("cmd", "/c", "echo", "test").start(); //this is for windows and our tests runs on "windows-latest"
        }
        processBuffer.put(mockprocess);
        assertEquals(mockprocess, processBuffer.get(), "Should match");
    }


    @Test
    void testEmptyAfterGet() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        Process mockprocess;
        if (os.contains("mac os")){
             mockprocess = new ProcessBuilder("echo", "test").start(); //this is for macOS and our tests runs on "windows-latest"
        }
        else {
             mockprocess = new ProcessBuilder("cmd", "/c", "echo", "test").start(); //this is for windows and our tests runs on "windows-latest"
        }
        processBuffer.put(mockprocess);
        processBuffer.get();
        assertTrue(processBuffer.isEmpty());
    }


    //Might have to add more tests, Not sure everything is covered.
}