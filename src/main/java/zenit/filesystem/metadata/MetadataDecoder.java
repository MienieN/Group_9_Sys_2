package main.java.zenit.filesystem.metadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Objects;

import main.java.zenit.filesystem.RunnableClass;

/**
 * The MetadataDecoder class is responsible for decoding metadata from a given file
 * and populating a Metadata object with the extracted information. The metadata file
 * is expected to follow a specific structure for parsing.
 */
public class MetadataDecoder {
    
    /**
     * Decodes metadata from the specified file and populates the given Metadata object with parsed information.
     * This method reads the metadata file line by line and determines the corresponding metadata fields to update.
     * It processes several aspects of metadata, such as version, directory, source path, JRE version, runnable classes,
     * internal libraries, and external libraries, based on specific metadata tags.
     *
     * @param metadataFile the file containing metadata information to be decoded
     * @param metadata     the Metadata object to be populated with data from the metadata file
     */
    public static void decode(File metadataFile, Metadata metadata) {
        try {
            //Read lines from file
            LinkedList <String> lines = readMetadata(metadataFile);
            
            String line;
            while ((line = getNextLine(lines)) != null){
                switch (line){
                    case "ZENIT METADATA":  // version
                        metadata.setVersion(getNextLine(lines));
                        
                    case "DIRECTORY":   // directory
                        metadata.setDirectory(getNextLine(lines));
                        
                    case "SOURCEPATH":  // sourcepath
                        metadata.setSourcePath(getNextLine(lines));
                        
                    case "JRE VERSION":  // JRE version
                        metadata.setJREVersion(getNextLine(lines));
                        
                    case "RUNNABLE CLASSES":  // runnable classes
                        decodeRunnableClasses(lines, metadata);
                        
                    case "INTERNAL LIBRARIES": // internal libraries
                        metadata.setInternalLibraries(decodeLibraryList(lines));
                        
                    case "EXTERNAL LIBRARIES":  // external libraries
                        metadata.setExternalLibraries(decodeLibraryList(lines));
                }
            }
        }
        catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        catch (NoSuchElementException e) {
            System.out.println("Error parsing metadata: " + e.getMessage());
        }
    }
    
    /**
     * Decodes a list of library names from the given linked list of lines.
     * The method expects the first line to represent the total number of libraries,
     * followed by the actual library names.
     *
     * @param lines a LinkedList of strings containing metadata lines, where the first line
     *              specifies the number of libraries, and subsequent lines
     *              include the library names.
     * @return an array of library names, where the size of the array corresponds
     *         to the number specified in the first line of the list.
     */
    private static String[] decodeLibraryList(LinkedList<String> lines) {
        int count = Integer.parseInt(Objects.requireNonNull(getNextLine(lines)));
        String[] libraries = new String[count];
        for (int i = 0; i < count; i++) {
            libraries[i] = getNextLine(lines);
        }
        return libraries;
    }
    
    /**
     * Retrieves and removes the first line from the provided list of lines.
     * If the list is empty, returns null.
     *
     * @param lines a LinkedList of strings representing the lines of metadata
     * @return the first line from the list if available, or null if the list is empty
     */
    private static String getNextLine(LinkedList<String> lines) {
        return lines.isEmpty() ? null : lines.removeFirst();
    }
    
    /**
     * Decodes and parses runnable class definitions from the given list of lines and updates the provided metadata object.
     * The method extracts information about runnable classes, including their paths and optional program and VM arguments.
     * Expects the first line to indicate the number of runnable classes, followed by detailed information for each class.
     *
     * @param lines a LinkedList of strings containing metadata lines, where the first line specifies the number
     *              of runnable classes, and subsequent lines contain their details such as paths, program arguments,
     *              and VM arguments.
     * @param metadata the Metadata object to be updated with the parsed runnable class information.
     */
    private static void decodeRunnableClasses(LinkedList<String> lines, Metadata metadata) {
        int nbrOfRunnableClasses = Integer.parseInt(Objects.requireNonNull(getNextLine(lines)));
        
        if (nbrOfRunnableClasses > 0) {
            RunnableClass[] runnableClasses = new RunnableClass[nbrOfRunnableClasses];
            
            for (int i = 0; i < nbrOfRunnableClasses; i++) {
                String path = null, programArguments = null, vmArguments = null;
                String line = getNextLine(lines);
                
                if ("RCLASS".equals(line)) {
                    path = getNextLine(lines);
                    line = getNextLine(lines);
                    
                    if ("PROGRAM ARGUMENTS".equals(line)) {
                        programArguments = getNextLine(lines);
                        line = getNextLine(lines);
                    }
                    
                    if ("VM ARGUMENTS".equals(line)) {
                        vmArguments = getNextLine(lines);
                    }
                }
                
                runnableClasses[i] = new RunnableClass(path, programArguments, vmArguments);
            }
            metadata.setRunnableClasses(runnableClasses);
        }
    }
    
    /**
     * Reads metadata information from the specified file line by line and returns the list of lines.
     * This method ensures that the file exists and is readable.
     * If the file does not exist or cannot be read, an IOException is thrown.
     *
     * @param metadataFile the file containing metadata information to be read
     * @return a LinkedList of strings containing all the lines from the metadata file
     * @throws IOException if the file does not exist or an error occurs while reading the file
     */
    private static LinkedList <String> readMetadata(File metadataFile) throws IOException {
        
        if (!metadataFile.exists()) {
            throw new IOException("Metadata don't exist: " + metadataFile.getAbsolutePath());
        }
        
        LinkedList <String> lines = new LinkedList <String>();
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader
                (new FileInputStream(metadataFile), "UTF-8"))) {
            
            String line = br.readLine();
            
            while (line != null) {
                lines.add(line);
                line = br.readLine();
            }
            return lines;
        }
        catch (IOException ex) {
            throw new IOException("Couldn't read metadata");
        }
    }
}
