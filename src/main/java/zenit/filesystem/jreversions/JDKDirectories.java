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
import java.util.Collections;
import java.util.List;

import main.java.zenit.Zenit;

/**
 This class is responsible for managing the JDK installation directories
 and their associated files. It provides methods for reading, writing, and
 updating the list of JDK installation directories, as well as for extracting
 the names of the JDK directories as strings. The class also provides methods
 for validating JDK directories and appending them to the list of known JDK
 installations, as well as for removing JDK directories from the list.
 */
public class JDKDirectories {
    /**
     The main entry point of the application. This method initializes
     the application by setting the default JDK file to null, effectively
     resetting any previously stored default JDK configuration.
     
     @param args command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        setDefaultJDKFile(null);
    }
    
    /**
     * Creates a new file to store information about detected Java Development Kit (JDK) directories
     * and populates it with a list of available Java Virtual Machine (JVM) installation directories
     * based on the default paths for the underlying operating system.
     * <p>
     * This method performs the following sequence of operations:
     * <p>1. Ensures that the file for serialized JDK directories exists, creating it if necessary.</p>
     * <p>2. Determines the default JVM installation directory based on the operating system.</p>
     * <p>3. Collects all available JVM directories from the identified default directory.</p>
     * <p>4. Serializes the list of collected JVM directories to the file if the list is not empty.</p>
     *	</p>
     * If an I/O error occurs during file creation or serialization, an error message is printed.
     */
    public static void createNewFileWithDefaultJVMDirectories() {
        try {
            // Area 1: Ensure the file for serialized JDK directories exists
            File file = getOrCreateJDKFile();
            
            // Area 2: Fetch JVM installation directory based on OS
            File javaFolder = getJVMDirectory();
            
            // Area 3: Collect JVM directories
            ArrayList <File> JVMsList = collectJVMDirectories(javaFolder);
            
            // Area 4: Serialize the collected JVM directories if the list is not empty
            if (! JVMsList.isEmpty()) {
                serializeJDKFiles(JVMsList);
            }
        }
        catch (IOException e) {
            System.err.println("An error occurred while creating the JDK file.");
        }
    }
    
    /**
     * Ensures the existence of a specific file intended to store information
     * about Java Development Kit (JDK) directories. If the file does not exist,
     * it is created.
     *
     * @return the File object representing the specified file.
     * @throws IOException if an I/O error occurs during file creation.
     */
    public static File getOrCreateJDKFile() throws IOException {
        File file = new File("res/JDK/JDK.dat");
        if (! file.exists()) {
            file.createNewFile();
        }
        return file;
    }
    
    /**
     * Retrieves the default directory for Java Virtual Machine (JVM) installations
     * based on the operating system.
     *
     * @return a File object representing the default JVM directory for the
     * operating system. Returns null if the operating system is unrecognized.
     */
    public static File getJVMDirectory() {
        String OS = Zenit.OS;
        if (OS.contains("Windows")){
            OS = "Windows";
        }
        else if (OS.contains("Mac os")){
            OS = "Mac OS";
        }


        
        // Using a switch-case to handle different OS values
        switch (OS) {
            case "Mac OS":
                return new File("/library/java/javavirtualmachines");
            case "Windows":
                return new File("C:\\Program Files\\Java\\");
            default:
                return null; // Default case if no match is found
        }
    }
    
    /**
     * Collects all directories within a specified Java folder, returning them as a list of File objects.
     * If the provided folder does not exist or is null, an empty list is returned.
     *
     * @param javaFolder the root directory that potentially contains Java Virtual Machine (JVM) directories.
     *                   If this folder is null or does not exist, no directories are collected.
     * @return a list of File objects representing the directories within the specified javaFolder.
     *         If no directories are found or javaFolder is invalid, the returned list will be empty.
     */
    public static ArrayList <File> collectJVMDirectories(File javaFolder) {
        ArrayList <File> JVMsList = new ArrayList <>();
        
        if (javaFolder != null && javaFolder.exists()) {
            File[] JVMs = javaFolder.listFiles();
            
            if (JVMs != null) {
                Collections.addAll(JVMsList, JVMs);
            }
        }
        return JVMsList;
    }
    
    /**
     * Reads a list of Java Development Kit (JDK) installation directories from a serialized file.
     * The method deserializes the contents of the file "res/JDK/JDK.dat", which is expected to
     * store the directories as File objects, and returns these directories in a list. If an
     * error occurs during file reading or deserialization, an empty list is returned.
     *
     * @return a list of File objects representing JDK installation directories, or an empty list if
     * the file is not found, contains no directories, or an error occurs during reading.
     */
    public static List <File> readJDKInstallationDirectoriesFromFile() {
        ArrayList <File> JDKs = new ArrayList <File>();
        File file;
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream("res/JDK/JDK.dat")))) {
            file = (File) ois.readObject();
            
            while (file != null) {
                JDKs.add(file);
                file = (File) ois.readObject();
            }
        }
        catch (IOException | ClassNotFoundException e) {
          System.err.println("An error occurred while reading the JDK file.");
        }
        
        return JDKs;
    }
    
    /**
     * Extracts the names of Java Development Kit (JDK) installation directories as a list of strings.
     * This method internally retrieves JDK directories by reading a serialized file and collects
     * the directory names into a list. If no directories are found, an empty list is returned.
     *
     * @return a list of strings representing the names of JDK installation directories. Returns
     * an empty list if no JDK directories are found or if an error occurs during retrieval.
     */
    public static List <String> extractJDKDirectoryNameAsString() {
        List <String> JDKsString = new ArrayList <String>();
        List <File> JDKs = readJDKInstallationDirectoriesFromFile();
        
        if (!JDKs.isEmpty()) {
            for (File JDK : JDKs) {
                JDKsString.add(JDK.getName());
            }
        }
        return JDKsString;
    }
    
    /**
     * Serializes a list of JDK-related files to a specified file, writing each file object
     * sequentially to the output stream. The file is saved as "res/JDK/JDK.dat".
     *
     * @param files the list of File objects to be serialized. Each file object in the list
     *              represents a JDK-related file that will be written to the output stream.
     */
    public static void serializeJDKFiles(List <File> files) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(
                new FileOutputStream("res/JDK/JDK.dat")))) {
            
            for (File file : files) {
                oos.writeObject(file);
            }
            oos.flush();
        }
        catch (IOException e) {
          System.err.println("An error occurred while serializing the JDK files.");
        }
    }
    
    /**
     * Appends a given JDK installation directory to the list of tracked directories
     * if the directory is valid. The method first validates the provided file as
     * a JDK installation and then adds it to the list of known JDK directories,
     * which is subsequently serialized to persist the updated list.
     *
     * @param file the File object representing the JDK installation directory to be appended.
     *             It must be a valid JDK directory containing both "java" and "javac" executables.
     * @return true if the directory was successfully appended and the list was serialized;
     *         false if the validation fails or any error occurs during the operation.
     */
    public static boolean appendToTrackedDirectoriesList (File file) {
        boolean success = false;
        
        if (JDKVerifier.validateJDKInstallation(file)) {
            List <File> files = readJDKInstallationDirectoriesFromFile();
            files.add(file);
            
            serializeJDKFiles(files);
            
            success = true;
        }
        return success;
    }
    
    /**
     * Removes the specified file from the list of tracked JDK installation directories.
     * If the file is successfully removed from the list, the updated list is serialized
     * for persistence.
     *
     * @param file the File object representing the JDK installation directory to be removed.
     *             This must be a tracked directory present in the serialized list.
     * @return true if the file was successfully removed and the updated list was serialized;
     *         false if the file was not found in the list or an error occurred during the operation.
     */
    public static boolean removeFromTrackedDirectoriesList(File file) {
        List <File> files = readJDKInstallationDirectoriesFromFile();
        boolean success = files.remove(file);
        
        if (success) {
            serializeJDKFiles(files);
        }
        return success;
    }
    
    /**
     * Retrieves the full path of a Java Development Kit (JDK) installation directory
     * based on its name. The method iterates through a list of known JDK directories
     * and finds the directory that matches the specified name.
     *
     * @param name the name of the JDK directory to search for. This should match the
     *             name of one of the known JDK installation directories.
     * @return the full path of the JDK directory as a string if a match is found;
     *         otherwise, returns null if no directory with the specified name exists.
     */
    public static String getFullPathFromName(String name) {
        List <File> JDKs = readJDKInstallationDirectoriesFromFile();
        
        for (File JDK : JDKs) {
            if (JDK.getName().equals(name)) {
                return JDK.getPath();
            }
        }
        return null;
    }
    
    /**
     * Sets the default Java Development Kit (JDK) file by serializing the specified file
     * to a predefined location on disk. If the predefined file does not exist, it will
     * be created. This method is used to update the default JDK configuration.
     *
     * @param file the File object representing the JDK file to be set as the default.
     *             This file will be serialized to "res/JDK/DefaultJDK.dat".
     */
    public static void setDefaultJDKFile(File file) {
        File defaultJDK = new File("res/JDK/DefaultJDK.dat");
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(
                new FileOutputStream(defaultJDK)))) {
            
            if (! defaultJDK.exists()) {
                defaultJDK.createNewFile();
            }
            
            oos.writeObject(file);
            oos.flush();
            
        }
        catch (IOException e) {
            System.err.println("An error occurred while setting the default JDK file.");
        }
    }
    
    /**
     * Retrieves the default Java Development Kit (JDK) file from a predefined location.
     * The method attempts to deserialize the file stored at "res/JDK/DefaultJDK.dat"
     * and returns it as a File object. If the file does not exist or an error occurs
     * during deserialization, null is returned.
     *
     * @return the File object representing the default JDK if successfully deserialized,
     *         or null if the file is not found or an error occurs during deserialization.
     */
    public static File getDefaultJDKFile() {
        File defaultJDK = new File("res/JDK/DefaultJDK.dat");
        
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
                new FileInputStream(defaultJDK)))) {
            return (File) ois.readObject();
        }
        catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}
