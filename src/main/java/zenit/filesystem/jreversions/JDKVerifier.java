package main.java.zenit.filesystem.jreversions;

import main.java.zenit.Zenit;

import java.io.File;

/**
 * The JDKVerifier class provides utility methods to validate the existence of
 * required tools, such as "java" and "javac", within a given JDK installation.
 * It also provides helper functionality to construct the executable paths
 * for these tools based on the JDK installation directory and the underlying
 * operating system.
 */
public class JDKVerifier {
    /**
     * Validates that the given JDK installation directory contains the required
     * "java" and "javac" executables.
     *
     * @param JDK the directory representing the root of the JDK installation.
     * @return true if both "java" and "javac" executables are found in the correct
     * locations within the given JDK installation directory; false otherwise.
     */
    public static boolean validateJDKInstallation(File JDK) {
        File java = new File(getExecutablePath(JDK.getPath(), "java"));
        File javac = new File(getExecutablePath(JDK.getPath(), "javac"));

        return java.exists() && javac.exists();
    }

    /**
     * Constructs the full path to an executable tool (e.g., "java" or "javac")
     * within a provided JDK installation directory based on the operating system.
     *
     * @param JDKPath the path to the root directory of the JDK installation.
     * @param tool    the name of the executable tool (e.g., "java", "javac").
     * @return the full path to the specified executable tool within the JDK installation.
     */
    public static String getExecutablePath(String JDKPath, String tool) {
        String OS = Zenit.OS.toLowerCase();
        String path = null;

        if (OS.contains("mac")) {
            path = JDKPath + File.separator + "Contents" + File.separator + "Home" + File.separator + "bin" + File.separator + tool;
        } else if (OS.contains("windows")) {
            path = JDKPath + File.separator + "bin" + File.separator + tool;
        } else {
            throw new IllegalArgumentException("Unsupported OS: " + OS);
        }

        return path;
    }
}
