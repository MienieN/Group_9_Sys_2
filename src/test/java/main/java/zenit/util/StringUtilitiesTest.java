package main.java.zenit.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilitiesTest {
    private StringUtilities stringUtilities;

    @BeforeEach
    void setup(){
        stringUtilities = new StringUtilities();
    }

    @Test
    void testCountLeadingSpacesNull(){
        String str = null;
        assertEquals(0, stringUtilities.countLeadingSpaces(str));
    }

    @Test
    void testCountLeadingSpacesEmpty(){
        String str = "";
        assertEquals(0, stringUtilities.countLeadingSpaces(str));
    }

    @Test
    void testCountLeadingSpacesStringSpaceAtStart(){
        String str = " test";
        assertEquals(1, stringUtilities.countLeadingSpaces(str));
    }

    @Test
    void testCountLeadingTwoSpacesStringSpaceAtStart(){
        String str = "  test";
        assertEquals(2, stringUtilities.countLeadingSpaces(str));
    }

    @Test
    void testCountLeadingSpacesString(){
        String str = "test";
        assertEquals(0, stringUtilities.countLeadingSpaces(str));
    }

    @Test
    void TestCountNull(){
        String str = null;
        assertEquals(0, stringUtilities.count(str, 'a'));
    }

    @Test
    void TestCountEmpty(){
        String str = "";
        assertEquals(0, stringUtilities.count(str, 'a'));
    }

    @Test
    void TestCountStringThreeA(){
        String str = "all animals";
        assertEquals(3, stringUtilities.count(str, 'a'));
    }
    @Test
    void TestCountStringThreeANotAtStart(){
        String str = "Not all animals";
        assertEquals(3, stringUtilities.count(str, 'a'));
    }

}