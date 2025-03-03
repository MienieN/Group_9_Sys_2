package main.java.zenit.filesystem.helpers;

import main.java.zenit.exceptions.TypeCodeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CodeSnippetsTest {
    @Test
    void testNewSnippetEmpty() throws TypeCodeException {
        String result = CodeSnippets.newSnippet(99, "Test.java", "com.example");
        assertEquals("", result, "Expected empty string for EMPTY type code");
    }

    @Test
    void testNewSnippetClass() throws TypeCodeException {
        String expected = "package com.example;\n\npublic class Test {\n\n}";
        String result = CodeSnippets.newSnippet(100, "Test.java", "com.example");
        assertEquals(expected, result, "Class snippet does not match expected output");
    }

    @Test
    void testNewSnippetInterface() throws TypeCodeException {
        String expected = "package com.example\n\npublic interface Test {\n\n}";
        String result = CodeSnippets.newSnippet(101, "Test.java", "com.example");
        assertEquals(expected, result, "Interface snippet does not match expected output");
    }

    @Test
    void testNewSnippetInvalidTypeCode() {
        assertThrows(TypeCodeException.class, () -> {
            CodeSnippets.newSnippet(999, "Test.java", "com.example");
        }, "Expected TypeCodeException for invalid type code");
    }

}