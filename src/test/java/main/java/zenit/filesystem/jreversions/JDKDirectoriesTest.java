package main.java.zenit.filesystem.jreversions;

import main.java.zenit.Zenit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

// Note: Not all methods in JDKDirectories is covered as I wasn't able to use Mockito for class mocking.
class JDKDirectoriesTest {
    @TempDir
    Path tempDir;

    @Test
    void testGetOrCreateJDKFile() throws IOException {
        Path tempFile = tempDir.resolve("JDK.dat");
        Files.createFile(tempFile);

        // Test that the file is created
        File file = JDKDirectories.getOrCreateJDKFile();
        assertNotNull(file);
        assertTrue(file.exists());
    }

    @Test
    void testGetJVMDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac os")) {
            assertEquals("/library/java/javavirtualmachines", JDKDirectories.getJVMDirectory().getPath()); //this is for macOS
        } else if (os.contains("windows")) {
            assertEquals("C:\\Program Files\\Java", JDKDirectories.getJVMDirectory().getPath()); //this is for windows
        } else assertNull(JDKDirectories.getDefaultJDKFile());
    }

    @Test
    void testCollectJVMDirectoriesWithValidFolder() throws IOException {
        // Skapa en temporär mapp med underkataloger
        Path tempJavaFolder = tempDir.resolve("java");
        Files.createDirectory(tempJavaFolder);
        Files.createDirectory(tempJavaFolder.resolve("jdk1.8.0"));
        Files.createDirectory(tempJavaFolder.resolve("jdk11"));

        // Testa att underkatalogerna samlas
        File javaFolder = tempJavaFolder.toFile();
        ArrayList<File> directories = JDKDirectories.collectJVMDirectories(javaFolder);
        assertEquals(2, directories.size());
        assertTrue(directories.contains(new File(tempJavaFolder.resolve("jdk1.8.0").toString())));
        assertTrue(directories.contains(new File(tempJavaFolder.resolve("jdk11").toString())));
    }

    @Test
    void testSetAndGetDefaultJDKFile() throws IOException {
        File tempJDK = File.createTempFile("jdk", ".test");
        tempJDK.deleteOnExit();

        JDKDirectories.setDefaultJDKFile(tempJDK);  // Bytt till JDKDirectories
        File result = JDKDirectories.getDefaultJDKFile();  // Bytt till JDKDirectories

        assertNotNull(result);
        assertEquals(tempJDK.getAbsolutePath(), result.getAbsolutePath());
    }

    @Test
    void testGetDefaultJDKFile_FileNotFound() {
        File defaultFile = new File("res/JDK/DefaultJDK.dat");
        if (defaultFile.exists()) {
            defaultFile.delete();
        }
        assertNull(JDKDirectories.getDefaultJDKFile());
    }

    @Test
    void testCollectJVMDirectoriesWithInvalidFolder() {
        // Testa med en mapp som inte finns
        File invalidDir = new File("/invalid/path");
        ArrayList<File> directories = JDKDirectories.collectJVMDirectories(invalidDir);
        assertTrue(directories.isEmpty());

        // Testa med null
        directories = JDKDirectories.collectJVMDirectories(null);
        assertTrue(directories.isEmpty());
    }
}