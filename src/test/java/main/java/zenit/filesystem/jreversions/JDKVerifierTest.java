package main.java.zenit.filesystem.jreversions;

import main.java.zenit.Zenit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

// Note: Not all methods in JDKDirectories is covered as I wasn't able to use Mockito for class mocking.
class JDKVerifierTest {

    @Test
    void testGetExecutablePath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac os")) {
            assertEquals("/path/to/jdk/Contents/Home/bin/javac", JDKVerifier.getExecutablePath("/path/to/jdk", "javac"));
            assertEquals("/path/to/jdk/Contents/Home/bin/java", JDKVerifier.getExecutablePath("/path/to/jdk", "java"));
        } else if (os.contains("windows")) {
            assertEquals("C:\\Program Files\\Java\\jdk\\bin\\javac", JDKVerifier.getExecutablePath("C:\\Program Files\\Java\\jdk", "javac"));
            assertEquals("C:\\Program Files\\Java\\jdk\\bin\\java", JDKVerifier.getExecutablePath("C:\\Program Files\\Java\\jdk", "java"));
        } else {
            assertNull(JDKVerifier.getExecutablePath("path", "javac"));
            assertNull(JDKVerifier.getExecutablePath("path", "java"));
        }

    }
}
