package main.java.zenit.filesystem;

import java.io.File;
import java.io.IOException;

public class ProjectFile extends File {

    private static final long serialVersionUID = -9201755155887621850L;
    private File metadata;
    private File src;
    private File bin;
    private File lib;

    /**
     * Constructs a ProjectFile with the specified pathname.
     *
     * @param pathname The path of the project directory.
     */
    public ProjectFile(String pathname) {
        super(pathname);
    }

    /**
     * Constructs a ProjectFile from an existing File object.
     *
     * @param file The file representing the project directory.
     */
    public ProjectFile(File file) {
        super(file.getPath());
    }

    /**
     * Adds a "lib" directory to the project if it does not exist.
     *
     * @return The created or existing "lib" directory.
     */
    public File addLib() {
        if (lib == null) {
            lib = createDirectory("lib");
        }
        return lib;
    }

    /**
     * Retrieves the "lib" directory.
     *
     * @return The "lib" directory, or null if not found.
     */
    public File getLib() {
        return (lib == null) ? (lib = findDirectory("lib")) : lib;
    }

    /**
     * Sets the "lib" directory.
     *
     * @param lib The "lib" directory to set.
     */
    public void setLib(File lib) {
        this.lib = lib;
    }

    /**
     * Adds a "src" directory to the project if it does not exist.
     *
     * @return The created or existing "src" directory.
     */
    public File addSrc() {
        if (src == null) {
            src = createDirectory("src");
        }
        return src;
    }

    /**
     * Retrieves the "src" directory.
     *
     * @return The "src" directory, or null if not found.
     */
    public File getSrc() {
        return (src == null) ? (src = findDirectory("src")) : src;
    }

    /**
     * Adds a "bin" directory to the project if it does not exist.
     *
     * @return The created or existing "bin" directory.
     */
    public File addBin() {
        if (bin == null) {
            bin = createDirectory("bin");
        }
        return bin;
    }

    /**
     * Retrieves the "bin" directory.
     *
     * @return The "bin" directory, or null if not found.
     */
    public File getBin() {
        return (bin == null) ? (bin = findDirectory("bin")) : bin;
    }

    /**
     * Adds a metadata file to the project if it does not exist.
     *
     * @return The created or existing metadata file, or null if an error occurs.
     */
    public File addMetadata() {
        metadata = getMetadata();
        if (metadata == null) {
            try {
                metadata = MetadataFileHandler.createMetadataFile(this);
            } catch (IOException e) {
                return null;
            }
        }
        return metadata;
    }

    /**
     * Retrieves the metadata file.
     *
     * @return The metadata file, or null if not found.
     */
    public File getMetadata() {
        return (metadata == null) ? (metadata = findDirectory(".metadata")) : metadata;
    }

    /**
     * Sets the metadata file.
     *
     * @param metadata The metadata file to set.
     */
    public void setMetadata(File metadata) {
        this.metadata = metadata;
    }

    /**
     * Creates a new subdirectory within the project.
     *
     * @param name The name of the directory.
     * @return The created directory file object.
     */
    private File createDirectory(String name) {
        File directory = new File(getPath() + File.separator + name);
        if (!directory.exists()) {
            directory.mkdir();
        }
        return directory;
    }

    /**
     * Searches for an existing directory within the project.
     *
     * @param name The name of the directory to find.
     * @return The found directory file object, or null if not found.
     */
    private File findDirectory(String name) {
        if (isDirectory()) {
            File[] files = listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().equals(name)) {
                        return file;
                    }
                }
            }
        }
        return null;
    }
}
