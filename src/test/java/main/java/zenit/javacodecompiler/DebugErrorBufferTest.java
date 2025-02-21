package main.java.zenit.javacodecompiler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DebugErrorBufferTest {

    DebugErrorBuffer debugErrorBuffer;

    @BeforeEach
    void setup(){
        debugErrorBuffer = new DebugErrorBuffer();
    }

    @Test
    void testPut(){
        DebugError debugError = new DebugError("place", "problemType", "problem", 4, 4);
        debugErrorBuffer.put(debugError);
        assertEquals(debugError, debugErrorBuffer.get());
    }

    @Test
    void testGet(){
        DebugError debugError = new DebugError("place", "problemType", "problem", 4, 4);
        debugErrorBuffer.put(debugError);
        assertEquals(debugError, debugErrorBuffer.get());
    }

    @Test
    void testIsEmpty(){
        assertTrue(debugErrorBuffer.isEmpty());
    }
}