package main.java.zenit.javacodecompiler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class DebugErrorTest {

    DebugError debugError;

    @BeforeEach
    void setup(){
        //Could improve the names on here
        debugError = new DebugError("place", "problemType", "problem", 4, 4);
    }

    @Test
    void testToString(){
        assertEquals("Place: place\nProblem type: problemType\nError: problem\n4:4", debugError.toString());
    }

    @Test
    void testGetPlace(){
        String place = "place";
        assertEquals(place, debugError.getPlace());
    }

    @Test
    void testGetProblemType(){
        String problemType = "problemType";
        assertEquals(problemType, debugError.getProblemType());
    }

    @Test
    void testGetProblem(){
        String problem = "problem";
        assertEquals(problem, debugError.getProblem());
    }

    @Test
    void testGetRow(){
        int row = 4;
        assertEquals(row, debugError.getRow());
    }

    @Test
    void testGetColumn(){
        int column = 4;
        assertEquals(column, debugError.getColumn());
    }
  
}