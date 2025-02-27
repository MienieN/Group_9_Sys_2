package main.java.zenit.filesystem.metadata;

import main.java.zenit.filesystem.RunnableClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MetadataTest {

    private Metadata metadata;
    private File tempFile;
    private File tempJavaFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("test_metadata", ".xml");
        tempJavaFile = File.createTempFile("test_javafile", ".java");
        tempFile.deleteOnExit(); // Ensures file is deleted when JVM exits
        metadata = new Metadata(tempFile);
    }

    @AfterEach
    void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete(); // Cleanup after each test
        }
    }

    @Test
    void testSetAndGetFile() {
        metadata.setFile(tempFile);
        assertEquals(tempFile, metadata.getFile());
    }

    @Test
    void testSetAndGetVersion() {
        metadata.setVersion("1.0");
        assertEquals("1.0", metadata.getVersion());
    }

    @Test
    void testSetAndGetDirectory() {
        metadata.setDirectory("/project");
        assertEquals("/project", metadata.getDirectory());
    }

    @Test
    void testSetAndGetSourcePath() {
        metadata.setSourcePath("/project/src");
        assertEquals("/project/src", metadata.getSourcePath());
    }

    @Test
    void testSetAndGetJREVersion() {
        metadata.setJREVersion("17");
        assertEquals("17", metadata.getJREVersion());
    }
    @Test
    void testSetAndGetRunnableClasses() {
        RunnableClass[] runnableClasses = {
                new RunnableClass("test1"),
                new RunnableClass("test2")
        };

        metadata.setRunnableClasses(runnableClasses);
        assertArrayEquals(runnableClasses, metadata.getRunnableClasses());
    }

    @Test
    void testAddRunnableClass() {
        RunnableClass rc1 = new RunnableClass(tempFile.getAbsolutePath());
        assertTrue(metadata.addRunnableClass(rc1));
        assertFalse(metadata.addRunnableClass(rc1)); // Should not allow duplicates
    }

    @Test
    void testRemoveRunnableClass() {
        RunnableClass rc1 = new RunnableClass(tempJavaFile.getPath());
        metadata.addRunnableClass(rc1);
        assertTrue(metadata.removeRunnableClass(rc1.getPath()));
    }

    @Test
    void testContainRunnableClass() {
        RunnableClass rc1 = new RunnableClass(tempJavaFile.getPath());
        metadata.addRunnableClass(rc1);
        assertEquals(rc1, metadata.containRunnableClass(rc1.getPath()));
        assertNull(metadata.containRunnableClass("UnknownClass"));
    }

    @Test
    void testSetAndGetInternalLibraries() {
        String[] internalLibs = {"lib1", "lib2", "lib3"};
        metadata.setInternalLibraries(internalLibs);
        assertArrayEquals(internalLibs, metadata.getInternalLibraries());
    }

    @Test
    void testSetInternalLibrariesWithNull() {
        metadata.setInternalLibraries(null);
        assertNull(metadata.getInternalLibraries());
    }

    @Test
    void testSetAndGetExternalLibraries() {
        String[] externalLibs = {"extLib1", "extLib2"};
        metadata.setExternalLibraries(externalLibs);
        assertArrayEquals(externalLibs, metadata.getExternalLibraries());
    }

    @Test
    void testSetExternalLibrariesWithNull() {
        metadata.setExternalLibraries(null);
        assertNull(metadata.getExternalLibraries());
    }

}