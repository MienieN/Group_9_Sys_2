package main.java.zenit.filesystem.metadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import main.java.zenit.filesystem.RunnableClass;

/**
 * The MetadataDecoder class is responsible for decoding metadata from a given file
 * and populating a Metadata object with the extracted information. The metadata file
 * is expected to follow a specific structure for parsing.
 */
public class MetadataDecoder {
    //TODO separate into smaller methods
    public static void decode(File metadataFile, Metadata metadata) {
        try {
            //Read lines from file
            LinkedList <String> lines = readMetadata(metadataFile);
            String line = lines.removeFirst();
            
            //Decode lines
            while (line != null) {
                //Version
                if (line.equals("ZENIT METADATA")) {
                    metadata.setVersion(lines.removeFirst());
                    
                    //Directory
                }
                else if (line.equals("DIRECTORY")) {
                    metadata.setDirectory(lines.removeFirst());
                    
                    //Sourcepath
                }
                else if (line.equals("SOURCEPATH")) {
                    metadata.setSourcePath(lines.removeFirst());
                    
                    //JRE version
                }
                else if (line.equals("JRE VERSION")) {
                    metadata.setJREVersion(lines.removeFirst());
                    
                    //Runnable classes
                }
                else if (line.equals("RUNNABLE CLASSES")) {
                    int nbrOfRunnableClasses = Integer.parseInt(lines.removeFirst());
                    if (nbrOfRunnableClasses != 0) {
                        RunnableClass[] runnableClasses = new RunnableClass[nbrOfRunnableClasses];
                        String path = null;
                        String pa = null;
                        String vma = null;
                        line = lines.removeFirst();
                        for (int i = 0; i < nbrOfRunnableClasses; i++) {
                            if (line.equals("RCLASS")) {
                                path = lines.removeFirst();
                                line = lines.removeFirst();
                                if (line.equals("PROGRAM ARGUMENTS")) {
                                    pa = lines.removeFirst();
                                    line = lines.removeFirst();
                                }
                                if (line.equals("VM ARGUMENTS")) {
                                    vma = lines.removeFirst();
                                    line = lines.removeFirst();
                                }
                                runnableClasses[i] = new RunnableClass(path, pa, vma);
                            }
                            if (i != nbrOfRunnableClasses - 1) {
                                line = lines.removeFirst();
                            }
                        }
                        metadata.setRunnableClasses(runnableClasses);
                    }
                    
                    //Internal libraries
                }
                else if (line.equals("INTERNAL LIBRARIES")) {
                    int nbrOfInternalLibraries = Integer.parseInt(lines.removeFirst());
                    if (nbrOfInternalLibraries != 0) {
                        String[] internalLibraries = new String[nbrOfInternalLibraries];
                        for (int i = 0; i < nbrOfInternalLibraries; i++) {
                            internalLibraries[i] = lines.removeFirst();
                        }
                        metadata.setInternalLibraries(internalLibraries);
                    }
                    
                    //External libraries
                }
                else if (line.equals("EXTERNAL LIBRARIES")) {
                    int nbrOfExternalLibraries = Integer.parseInt(lines.removeFirst());
                    if (nbrOfExternalLibraries != 0) {
                        String[] externalLibraries = new String[nbrOfExternalLibraries];
                        for (int i = 0; i < nbrOfExternalLibraries; i++) {
                            externalLibraries[i] = lines.removeFirst();
                        }
                        metadata.setExternalLibraries(externalLibraries);
                    }
                }
                line = lines.removeFirst();
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        catch (NoSuchElementException e) {
        }
    }
    
    private static LinkedList <String> readMetadata(File metadataFile) throws IOException {
        
        if (! metadataFile.exists()) {
            throw new IOException("Metadata don't exist");
        }
        
        LinkedList <String> lines = new LinkedList <String>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(metadataFile), "UTF-8"))) {
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
