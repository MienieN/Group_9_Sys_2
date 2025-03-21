package main.java.zenit.filesystem;

import java.io.File;

/**
 * Represents a runnable file with associated program and VM arguments.
 */
public class RunnableClass {

    private String path;
    private String paArguments;
    private String vmArguments;

    /**
     * Constructs a RunnableFile object with specified path, program arguments, and VM arguments.
     *
     * @param path        The file path.
     * @param paArguments The program arguments.
     * @param vmArguments The VM arguments.
     */
    public RunnableClass(String path, String paArguments, String vmArguments) {
        this.path = path;
        this.paArguments = paArguments;
        this.vmArguments = vmArguments;
    }

    /**
     * Constructs a RunnableFile object with specified path and default arguments.
     *
     * @param path The file path.
     */
    public RunnableClass(String path) {
        this(path, "", "");
    }

    /**
     * Constructs a RunnableFile object from a File object.
     *
     * @param file The File object.
     */
    public RunnableClass(File file) {
        this(file.getPath());
    }

    /**
     * Returns the file path.
     *
     * @return The file path.
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the file path.
     *
     * @param path The file path.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Returns the program arguments.
     *
     * @return The program arguments.
     */
    public String getPaArguments() {
        return paArguments;
    }

    /**
     * Sets the program arguments.
     *
     * @param paArguments The program arguments.
     */
    public void setPaArguments(String paArguments) {
        this.paArguments = paArguments;
    }

    /**
     * Returns the VM arguments.
     *
     * @return The VM arguments.
     */
    public String getVmArguments() {
        return vmArguments;
    }

    /**
     * Sets the VM arguments.
     *
     * @param vmArguments The VM arguments.
     */
    public void setVmArguments(String vmArguments) {
        this.vmArguments = vmArguments;
    }

    /**
     * Returns a string representation of the RunnableFile, including path, program arguments, and VM arguments.
     *
     * @return A string representation of the RunnableFile.
     */
    public String toString() {
        String nl = "\n";
        String string = path + nl + paArguments + nl + vmArguments;
        return string;
    }
}
