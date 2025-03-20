package main.java.zenit.javacodecompiler;

import java.io.File;
import java.util.regex.Matcher;

import main.java.zenit.filesystem.jreversions.JDKVerifier;
import main.java.zenit.filesystem.jreversions.JDKDirectories;

/**
 * CommandBuilder class is responsible for constructing and managing commands to compile or run Java programs.
 * It handles the configuration of the JDK, directories, libraries, and arguments.
 */
public class CommandBuilder {

    public static final String RUN = "java";
    public static final String COMPILE = "javac";

    private String tool;
    private String JDK;
    private String directory;
    private String sourcepath;
    private String[] internalLibraries;
    private String[] externalLibraries;
    private String[] libraries;
    private String runPath;
    private String programArguments;
    private String VMArguments;

    /**
     * Constructs a new CommandBuilder with the specified tool.
     *
     * @param tool The tool to be used (e.g., "java" or "javac").
     */
    public CommandBuilder(String tool) {
        this.tool = tool;
    }

    /**
     * Sets the JDK for the command. If a specific JDK is provided, it will be used; otherwise, the default JDK will be used.
     *
     * @param JDK The JDK version or path.
     */
    public void setJDK(String JDK) {
        this.JDK = (JDK != null) ? JDKVerifier.getExecutablePath(JDK, tool) : getDefaultJDK();
    }

    /**
     * Retrieves the default JDK path if available.
     *
     * @return The default JDK path.
     */
    private String getDefaultJDK() {
        String defaultJDK = JDKDirectories.getDefaultJDKFile().getPath();
        return (defaultJDK != null) ? JDKVerifier.getExecutablePath(defaultJDK, tool) : tool;
    }

    /**
     * Sets the working directory for the command.
     *
     * @param directory The directory path.
     */
    public void setDirectory(String directory) {
        this.directory = directory;
    }

    /**
     * Sets the source path for the Java source files.
     *
     * @param sourcepath The source path.
     */
    public void setSourcepath(String sourcepath) {
        this.sourcepath = "-sourcepath " + sourcepath;
    }

    /**
     * Sets the internal libraries for the Java program.
     *
     * @param internalLibraries An array of internal libraries.
     */
    public void setInternalLibraries(String[] internalLibraries) {
        this.internalLibraries = internalLibraries;
    }

    /**
     * Sets the external libraries for the Java program.
     *
     * @param externalLibraries An array of external libraries.
     */
    public void setExternalLibraries(String[] externalLibraries) {
        this.externalLibraries = externalLibraries;
    }

    /**
     * Sets the path of the Java program to be run.
     *
     * @param runPath The path to the program.
     */
    public void setRunPath(String runPath) {
        this.runPath = runPath;
    }

    /**
     * Sets additional arguments to be passed to the Java program during execution.
     *
     * @param programArguments The program arguments.
     */
    public void setProgramArguments(String programArguments) {
        this.programArguments = programArguments;
    }

    /**
     * Sets the VM arguments for running the Java program.
     *
     * @param VMArguments The VM arguments.
     */
    public void setVMArguments(String VMArguments) {
        this.VMArguments = VMArguments;
    }

    /**
     * Merges the internal and external libraries into a single array.
     */
    private void mergeLibraries() {
        int intLength = internalLibraries != null ? internalLibraries.length : 0;
        int extLength = externalLibraries != null ? externalLibraries.length : 0;

        libraries = new String[intLength + extLength];

        for (int i = 0; i < libraries.length; i++) {
            if (i < intLength) {
                libraries[i] = internalLibraries[i];
            } else {
                libraries[i] = externalLibraries[i - intLength];
            }
        }
    }

    /**
     * Generates the command to be executed based on the provided configurations.
     *
     * @return The constructed command as a string.
     */
    public String generateCommand() {
        StringBuilder command = new StringBuilder(JDK);

        if (VMArguments != null) {
            command.append(" ").append(VMArguments);
        }

        mergeLibraries();

        appendClassPath(command);
        appendDirectory(command);
        appendSourcePath(command);
        appendRunPath(command);
        appendProgramArguments(command);

        return command.toString();
    }

    /**
     * Appends the classpath to the command.
     *
     * @param command The command string builder.
     */
    private void appendClassPath(StringBuilder command) {
        if (tool.equals(RUN) && directory != null) {
            command.append(" -cp ./" + directory);
        }

        if (libraries != null) {
            appendLibraries(command);
        }
    }

    /**
     * Appends the library paths to the command.
     *
     * @param command The command string builder.
     */
    private void appendLibraries(StringBuilder command) {
        if (tool.equals(COMPILE)) {
            command.append(" -cp ").append(libraries[0]);
            if (libraries.length > 1) {
                for (int i = 1; i < libraries.length; i++) {
                    command.append(":").append(libraries[i]);
                }
            }
        } else if (tool.equals(RUN)) {
            for (String library : libraries) {
                command.append(":").append(".").append(File.separator).append(library);
            }
            command.append(":.");
        }
    }

    /**
     * Appends the directory option to the command for the compile tool.
     *
     * @param command The command string builder.
     */
    private void appendDirectory(StringBuilder command) {
        if (directory != null && tool.equals(COMPILE)) {
            command.append(" -d ").append(directory);
        }
    }

    /**
     * Appends the sourcepath option to the command.
     *
     * @param command The command string builder.
     */
    private void appendSourcePath(StringBuilder command) {
        if (sourcepath != null) {
            command.append(" ").append(sourcepath);
        }
    }

    /**
     * Appends the run path to the command.
     *
     * @param command The command string builder.
     */
    private void appendRunPath(StringBuilder command) {
        if (runPath != null) {
            if (tool.equals(RUN)) {
                runPath = runPath.replaceAll(Matcher.quoteReplacement(File.separator), "/");
            }
            command.append(" ").append(runPath);
        }
    }

    /**
     * Appends the program arguments to the command.
     *
     * @param command The command string builder.
     */
    private void appendProgramArguments(StringBuilder command) {
        if (programArguments != null) {
            command.append(" ").append(programArguments);
        }
    }
}
