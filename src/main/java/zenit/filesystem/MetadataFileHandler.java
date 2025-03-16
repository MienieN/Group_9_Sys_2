package main.java.zenit.filesystem;

import main.java.zenit.filesystem.jreversions.JDKDirectories;
import main.java.zenit.filesystem.metadata.Metadata;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

/**
 * The MetadataFileHandler class extends FileHandler and provides utility methods for managing
 * metadata files associated with projects. It includes functionality for creating, updating,
 * and modifying metadata files used in the project structure.
 */
public class MetadataFileHandler extends FileHandler {
    //TODO: Update this version number when a new version is released
    public final static String LATEST_VERSION = "2.2.1";

    /**
     * Creates a metadata file for a given project.
     *
     * @param projectFile The project file for which metadata is created.
     * @return The created metadata file.
     * @throws IOException If the file already exists or cannot be created.
     */
    protected static File createMetadataFile(File projectFile) throws IOException {
        File metadataFile = new File(projectFile.getPath() + File.separator + ".metadata");
        boolean success = metadataFile.createNewFile();
        if (!success) {
            throw new IOException("File already exist");
        }

        Metadata metadata = initializeMetadataDefaults(new Metadata(metadataFile));
        metadata.encode();

        return metadataFile;
    }

    /**
     * Updates an existing metadata file with default values if missing.
     *
     * @param metadataFile The metadata file to update.
     * @return The updated metadata object, or null if encoding fails.
     */
    protected static Metadata updateMetadata(File metadataFile) {
        Metadata metadata = initializeMetadataDefaults(new Metadata(metadataFile));
        return metadata.encode() ? metadata : null;
    }

    /**
     * Initializes metadata with default values.
     *
     * @param metadata The metadata object to initialize.
     * @return The initialized metadata object.
     */
    protected static Metadata initializeMetadataDefaults(Metadata metadata) {

        if (metadata.getVersion() == null || !metadata.getVersion().equals(LATEST_VERSION)) {
            metadata.setVersion(LATEST_VERSION);
        }
        if (metadata.getDirectory() == null) {
            metadata.setDirectory("bin");
        }
        if (metadata.getSourcePath() == null) {
            metadata.setSourcePath("src");
        }
        if (metadata.getJREVersion() == null || metadata.getJREVersion().equals("unknown")) {
            String jrePath = JDKDirectories.getDefaultJDKFile().getPath();
            metadata.setJREVersion(jrePath);
        }
        if (metadata.getRunnableClasses() == null) {
            metadata.setRunnableClasses(null);
        }
        if (metadata.getInternalLibraries() == null) {
            metadata.setInternalLibraries(null);
        }
        if (metadata.getExternalLibraries() == null) {
            metadata.setExternalLibraries(null);
        }

        return metadata;
    }

    /**
     * Changes the directory path in metadata.
     *
     * @param directory   The new directory path.
     * @param projectFile The project file whose metadata is updated.
     * @param internal    Whether the path should be relative.
     * @return The updated directory path.
     */
    protected static String changeDirectory(File directory, ProjectFile projectFile, boolean internal) {
        String directoryPath = directory.getPath();

        if (internal) {
            directoryPath = directoryPath.replaceFirst(Matcher.quoteReplacement(projectFile.getPath() + File.separator), "");
        }

        Metadata metadata = new Metadata(projectFile.getMetadata());
        metadata.setDirectory(directoryPath);
        metadata.encode();

        return directoryPath;
    }

    /**
     * Changes the source path in metadata.
     *
     * @param directory   The new source directory path.
     * @param projectFile The project file whose metadata is updated.
     * @param internal    Whether the path should be relative.
     * @return The updated source path.
     */
    protected static String changeSourcePath(File directory, ProjectFile projectFile, boolean internal) {
        String sourcePath = directory.getPath();

        if (internal) {
            sourcePath = sourcePath.replaceFirst(Matcher.quoteReplacement(projectFile.getPath() + File.separator), "");
        }

        Metadata metadata = new Metadata(projectFile.getMetadata());

        metadata.setSourcePath(sourcePath);
        metadata.encode();

        return sourcePath;
    }
}
