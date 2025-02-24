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
	
	//TODO separate the Java verification from the Javac verification
	public static boolean validJDK(File JDK) {
		
		File java = new File(getExecutablePath(JDK.getPath(), "java"));
		File javac = new File(getExecutablePath(JDK.getPath(), "javac"));
		
		return (java != null && javac != null && java.exists() && javac.exists());
	}
	
	public static String getExecutablePath(String JDKPath, String tool) {
		String OS = Zenit.OS;
		String path = null;
		
		if (OS.equals("Mac OS X")) {
			path = JDKPath + File.separator + "Contents" + File.separator + 
					"Home" + File.separator + "bin" + File.separator + tool;
		} else if (OS.equals("Windows")) {
			path = JDKPath + File.separator + "bin" + File.separator + tool;
		} else if (OS.equals("Linux")) {
			path = JDKPath + File.separator + "bin" + File.separator + tool;
		}
		
		return path;
	}
}
