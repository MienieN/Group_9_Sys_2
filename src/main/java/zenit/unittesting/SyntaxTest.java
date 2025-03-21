package main.java.zenit.unittesting;

import main.java.zenit.zencodearea.Syntax;
import org.antlr.runtime.RecognitionException;
import org.antlr.v4.tool.Grammar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SyntaxTest {
    private Grammar grammar;
    private Map<String, String> styleMap;
    private Syntax syntax;


    @BeforeEach
    public void setUp() throws RecognitionException {
        grammar = new TestGrammar();


        styleMap = new HashMap<>();
        styleMap.put("keyword", "blue");
        styleMap.put("string", "green");


        syntax = new Syntax(grammar, styleMap);
    }


    @Test
    public void testGetStyles() {
        Map<String, String> styles = syntax.getStyles();
        assertNotNull(styles);
        assertTrue(styles.containsKey("keyword"));
        assertEquals("blue", styles.get("keyword"));
    }


    @Test
    public void testGetGrammar() {
        Grammar result = syntax.getGrammar();
        assertNotNull(result);
        assertEquals("testGrammar", result.getATN());
    }


    private static class TestGrammar extends Grammar implements main.java.zenit.unittesting.TestGrammar {
        public TestGrammar() throws RecognitionException {
            super("testGrammar", "test");
        }
    }

}
