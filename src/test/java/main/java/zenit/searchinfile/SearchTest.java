package main.java.zenit.searchinfile;

import javafx.application.Platform;
import main.java.zenit.ui.MainController;
import main.java.zenit.zencodearea.ZenCodeArea;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SearchTest extends ApplicationTest {

    @Mock
    private ZenCodeArea zenCodeArea;

    @Mock
    private MainController mainController;


    private File testFile;
    private Search search;


    @BeforeEach
    void setUp() throws IOException, TimeoutException {

        MockitoAnnotations.openMocks(this);
        testFile = new File("testFile.txt");
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("This is a test file containing some text to test the SearchTest class." +
                    "\nTo test this class I have decided that this textFile should contain the word test a number of times." +
                    "\nThe number of times the word test should be found in this text should now be 6 since it counts the sneaky Test in SearchTest because the default value of caseSensetive is false." +
                    "\nWell now it should be 8!");
        }

        Platform.runLater(() ->{
        search = new Search(zenCodeArea, testFile, false, mainController);
        });
    }

    @Test
    void testSearchInFileStringTestCaseSensetiveFalse(){
        Platform.runLater(() -> {
        int numberOfHits = search.searchInFile("test");
        assertEquals(8,numberOfHits, "The word test should be found 8 times in the text with caseSensetive =false.");
        });
    }

    @Test
    void testSearchInFileStringTestCaseSensitiveTrue(){
        Platform.runLater(() -> {
            search.setCaseSensetive(true);
            int numberOfHits = search.searchInFile("test");
            assertEquals(5,numberOfHits, "When caseSensetive = true there should be 5 hits on the word 'test'");
        });
    }

    @Test
    void testSearchInFileNull(){
        Platform.runLater(() -> assertThrows(NullPointerException.class, () -> search.searchInFile(null)));
    }

    @Test
    void testSearchInFileWordNotInTestFile(){
        Platform.runLater(() -> {
            int numberOfHits = search.searchInFile("donkey");
            assertEquals(0,numberOfHits, "The word 'donkey' should not be found in the testFile");
        });
    }

    @Test
    void testReplaceOneWordFoundCaseSenFalse(){
        Platform.runLater(() -> {
            search.searchInFile("test");
            search.replaceOne("banana");
            verify(zenCodeArea, times(1)).replaceText(anyInt(), anyInt(), eq("banana"));
        });
    }

    @Test
    void testReplaceOneWordNotFoundCaseSenFalse(){
        Platform.runLater(() -> {
            search.searchInFile("donkey");
            assertThrows(IndexOutOfBoundsException.class, () -> search.replaceOne("banana"));
            verifyNoInteractions(zenCodeArea);
        });
    }

    @Test
    void testReplaceOneWordFoundCaseSenTrue(){
        Platform.runLater(() -> {
            search.setCaseSensetive(true);
            search.searchInFile("test");
            search.replaceOne("banana");
            verify(zenCodeArea, times(1)).replaceText(anyInt(),anyInt(), eq("banana"));
        });
    }

    @Test
    void testReplaceOneWordNotFoundCaseSenTrue(){
        Platform.runLater(() -> {
            search.setCaseSensetive(true);
            search.searchInFile("Donkey");
            assertThrows(IndexOutOfBoundsException.class, () -> search.replaceOne("banana"));
            verifyNoInteractions(zenCodeArea);
        });
    }

    @Test
    void testReplaceAllWordFoundCaseSenFalse(){
        Platform.runLater(() -> {
            search.searchInFile("test");
            search.replaceAll("banana");
            verify(zenCodeArea,  times(8)).replaceText(anyInt(), anyInt(), eq("banana"));
        });
    }

    @Test
    void testReplaceAllWordNotFoundCaseSenFalse(){
        Platform.runLater(() -> {
            search.searchInFile("donkey");
            search.replaceAll("banana");
            verify(zenCodeArea,  times(0)).replaceText(anyInt(), anyInt(), eq("banana"));
        });
    }

    @Test
    void testReplaceAllWordFoundCaseSenTrue(){
        Platform.runLater(() -> {
            search.setCaseSensetive(true);
            search.searchInFile("test");
            search.replaceAll("banana");
            verify(zenCodeArea, times(5)).replaceText(anyInt(), anyInt(), eq("banana"));
        });
    }

    @Test
    void testReplaceAllWordNOtFoundCaseSenTrue(){
        Platform.runLater(() -> {
            search.setCaseSensetive(true);
            search.searchInFile("Donkey");
            search.replaceAll("banana");
            verify(zenCodeArea, times(0)).replaceText(anyInt(), anyInt(), eq("banana"));
        });
    }

    @Test
    void testClearZenFoundWordCaseSenFalse(){
        Platform.runLater(() -> {
            search.searchInFile("test");
            search.clearZen();
            verify(zenCodeArea, times(8)).clearStyle(anyInt(), anyInt());
        });
    }

    @Test
    void TestClearZenTwo(){
        Platform.runLater(() -> {
            search.searchInFile("test");
            search.cleanZen();
            verify(zenCodeArea, atLeastOnce()).appendText(" ");
            verify(zenCodeArea, atLeastOnce()).deletePreviousChar();
        });
    }

    @Test
    void testClearZenWordNotFoundCaseSenFalse(){
        Platform.runLater(() -> {
            search.searchInFile("donkey");
            search.cleanZen();
            verify(zenCodeArea, times(0)).clearStyle(anyInt(), anyInt());
        });
    }

    @Test
    void testJumpDown(){
        Platform.runLater(() -> {
            search.searchInFile("test");

        int newIndex= search.jumpDown();
        assertEquals(1, newIndex, "This should be one after jumping down one because of the (i < absolutePos.size() - 1) in method");
        });
    }

    @Test
    void testJumpUp(){
        Platform.runLater(() -> {
            search.searchInFile("test");

            int newIndex = search.jumpUp();
            assertEquals(7, newIndex, "This should be 7 after jumping up 1 time because of how the (i = absolutePos.size() - 1;) in method");
        });

    }




}