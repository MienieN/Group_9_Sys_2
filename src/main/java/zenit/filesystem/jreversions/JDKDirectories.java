package main.java.zenit.filesystem.jreversions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import main.java.zenit.Zenit;

/**
 * This class is responsible for managing the JDK installation directories
 * and their associated files. It provides methods for reading, writing, and
 * updating the list of JDK installation directories, as well as for extracting
 * the names of the JDK directories as strings. The class also provides methods
 * for validating JDK directories and appending them to the list of known JDK
 * installations, as well as for removing JDK directories from the list.
 */
public class JDKDirectories {
	//TODO separate the methods to show better separation of concerns
	public static void main(String[] args) {
		setDefaultJDKFile(null);
	}

	public static void createNew() {
		try {
			File file = new File("res/JDK/JDK.dat");

			if (!file.exists()) {
				file.createNewFile();
			}

			ArrayList<File> JVMsList = new ArrayList<File>();
			File javaFolder = getJVMDirectory();

			if (javaFolder != null && javaFolder.exists()) {

				File[] JVMs = javaFolder.listFiles();
				for (File JVM : JVMs) {
					JVMsList.add(JVM);
				}

				if (JVMsList.size() > 0) {
					serializeJDKFiles(JVMsList);
				}
			}

		} catch (IOException e) {
		}
	}
	

	/**
	 * Reads a serialized list of JDK installation directories from a file
	 * and returns it as a list of File objects. If the file cannot
	 * be read or the data is invalid, an empty list is returned.
	 *
	 * @return a list of File objects representing JDK installation directories, or an empty list if the file cannot be read or no data is available.
	 */
	public static List<File> readJDKInstallationDirectoriesFromFile() {
		
		ArrayList<File> JDKs = new ArrayList<File>();
		File file;
		
		try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
				new FileInputStream("res/JDK/JDK.dat")))) {
			file = (File) ois.readObject();
			
			while (file != null) {
				JDKs.add(file);
				file = (File) ois.readObject();
			}
			
		
		} catch (IOException | ClassNotFoundException e) {
			
		}
		
		return JDKs;
	}
	
	/**
	 * Reads a list of JDK installation directories, extracts their names,
	 * and returns them as a list of strings.
	 *
	 * @return a list of strings representing the names of JDK installation directories,
	 * or an empty list if no JDK directories are found.
	 */
	public static List<String> extractJDKDirectoryNameAsString() {
		List<String> JDKsString = new ArrayList<String>();
		List<File> JDKs = readJDKInstallationDirectoriesFromFile();

		if (JDKs.size() > 0) {
			for (File JDK : JDKs) {
				JDKsString.add(JDK.getName());
			}
		}

		return JDKsString;

	}
	
	/**
	 * Writes the provided list of File objects to a serialized data file.
	 * This method serializes the list of JDK installation directories to a file
	 * named "res/JDK/JDK.dat" for later retrieval or processing.
	 *
	 * @param files the list of File objects to be serialized and written to the file.
	 */
	public static void serializeJDKFiles(List<File> files) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(
				new FileOutputStream("res/JDK/JDK.dat")))) {
			
			for (File file : files) {
				oos.writeObject(file);
			}
			oos.flush();
		} catch (IOException e) {
			
		}
	}
	
	/**
	 * Appends a valid JDK directory to the list of known JDK installations if the directory
	 * contains the necessary executables (e.g., java and javac). The JDK installation directories
	 * are read from a serialized file, updated with the new directory, and then re-serialized.
	 *
	 * @param file the JDK directory to be added to the list of known JDK installations
	 * @return true if the directory was successfully validated and added, false otherwise
	 */
	public static boolean appendToList(File file) {
		boolean success = false;
		
		if (JDKVerifier.validJDK(file)) {
			List<File> files = readJDKInstallationDirectoriesFromFile();
			files.add(file);
			
			serializeJDKFiles(files);
			
			success = true;
		}
		
		return success;

	}
	
	/**
	 * Removes the specified JDK directory from the list of known JDK installation directories.
	 * If the directory is successfully removed, the updated list is serialized and saved.
	 *
	 * @param file the JDK directory to be removed from the list of known JDK installations
	 * @return true if the directory was successfully removed, false otherwise
	 */
	public static boolean removeFromList(File file) {
		List<File> files = readJDKInstallationDirectoriesFromFile();
		boolean success = files.remove(file);
		
		if (success) {
			serializeJDKFiles(files);
		}
		
		return success;
	}
	
	/**
	 * Determines the default directory for Java Virtual Machine (JVM)
	 * installations based on the operating system.
	 *
	 * @return a File object representing the JVM installation directory,
	 *         or null if the operating system is unsupported or unrecognized.
	 */
	public static File getJVMDirectory() {
		String OS = Zenit.OS;
		if (OS.equals("Mac OS X")) {
			return new File("/library/java/javavirtualmachines");
		} else if (OS.equals("Windows")) {
			return new File("C:\\Program Files\\Java\\");
		}
		
		return null;
	}
	
	/**
	 * Retrieves the full path of a JDK installation directory based on its name.
	 * This method iterates through a list of known JDK installation directories
	 * and checks for a match with the given name. If a match is found, the full
	 * path of the corresponding directory is returned. If no match is found,
	 * the method returns null.
	 *
	 * @param name the name of the JDK installation directory to search for
	 * @return the full path of the JDK installation directory as a String if the name matches,
	 *         or null if no matching directory is found
	 */
	public static String getFullPathFromName(String name) {
		List<File> JDKs = readJDKInstallationDirectoriesFromFile();
		
		for (File JDK : JDKs) {
			if (JDK.getName().equals(name)) {
				return JDK.getPath();
			}
		}
		
		return null;
	}

	/**
	 * Sets a given JDK file as the default JDK by serializing it to a predefined location.
	 * The method writes the provided JDK file to a serialized file named "res/JDK/DefaultJDK.dat".
	 * If the file does not exist, it is created. If an I/O error occurs during the process,
	 * the error is silently caught without throwing exceptions.
	 *
	 * @param file the JDK file to be set as the default JDK.
	 */
	public static void setDefaultJDKFile(File file) {
		File defaultJDK = new File("res/JDK/DefaultJDK.dat");
			
		try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(
				new FileOutputStream(defaultJDK)))) {

			if (!defaultJDK.exists()) {
				defaultJDK.createNewFile();
			}
			
			oos.writeObject(file);
			oos.flush();

		} catch (IOException e) {
		}
	}
	
	/**
	 * Retrieves the default JDK file from a predefined serialized file location.
	 * The method attempts to deserialize and read the default JDK file stored in "res/JDK/DefaultJDK.dat".
	 * If the file cannot be read or deserialization fails, the method returns null.
	 *
	 * @return a File object representing the default JDK file, or null if an error occurs during reading or deserialization.
	 */
	public static File getDefaultJDKFile() {
		
		File defaultJDK = new File("res/JDK/DefaultJDK.dat");
		
		try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
				new FileInputStream(defaultJDK)))) {
			return (File) ois.readObject();
			
		} catch (IOException | ClassNotFoundException e) {
			return null;
		}
		
	}
}
