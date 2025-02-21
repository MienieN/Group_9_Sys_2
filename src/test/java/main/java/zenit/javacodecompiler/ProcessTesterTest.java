package main.java.zenit.javacodecompiler;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ProcessTesterTest {

    @Test
    void testProcessTester() throws IOException, InterruptedException {
        Process process = new ProcessBuilder("ping", "127.0.0.1", "-n", "5").start();

        ProcessTester processTester = new ProcessTester(process);
        processTester.start(); //processTester.run() says it should be replaced with start();

        processTester.join();

        assertFalse(process.isAlive(), "ProcessTester should destroy the process in its run method");
    }

}