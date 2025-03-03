package main.java.zenit.console;

public class ConsoleAreaErrorStreamTest {
    private ConsoleArea consoleAreaMock;
    private ConsoleAreaErrorStream consoleAreaErrorStream;

    // JavaFX components (ConsoleArea, which extends JavaFX components) require the JavaFX Toolkit to be initialized before they can be used in tests.
    /*
    @BeforeEach
    void setUp() {
        consoleAreaMock = new ConsoleArea();
        consoleAreaErrorStream = new ConsoleAreaErrorStream(consoleAreaMock);
    }

    @Test
    void testFlush() {
        consoleAreaErrorStream.getStringBuilder().append("test");
        consoleAreaErrorStream.flush();
        verify(consoleAreaMock).printError("test");
        assertEquals(0, consoleAreaErrorStream.getStringBuilder().length());
    }

    @Test
    void testWriteNewLine() throws IOException {
        consoleAreaErrorStream.write('\n');
        verify(consoleAreaMock).printError("\n");
        assertEquals(0, consoleAreaErrorStream.getStringBuilder().length());
    }

    @Test
    void testWriteSingleCharacter() throws IOException {
        consoleAreaErrorStream.write('a');
        verify(consoleAreaMock).printError("a");
        assertEquals("a", consoleAreaErrorStream.getStringBuilder().toString());
    }

    @Test
    void testWriteByteArray() throws IOException {
        byte[] bytes = "helloTest".getBytes();
        consoleAreaErrorStream.write(bytes, 0, bytes.length);
        verify(consoleAreaMock).printError("helloTest");
    }

     */

}
