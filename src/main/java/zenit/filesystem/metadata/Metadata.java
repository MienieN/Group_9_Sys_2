package main.java.zenit.filesystem.metadata;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.java.zenit.filesystem.RunnableClass;

/**
 * The Metadata class is designed to hold and manage metadata information about a project.
 * This includes details such as the version, directory path, source path, JRE version,
 * runnable classes, as well as internal and external libraries. The Metadata class
 * provides functionality to encode and decode metadata information for persistent storage.
 */
public class Metadata {
	// variables to store metadata information:
	private File metadataFile;
	private String version, directory, sourcePath, JREVersion;
	private RunnableClass[] runnableClasses;
    private final RunnableClass[] runnableClassesTemp = new RunnableClass[0];
	private String[] internalLibraries, externalLibraries;

	// ------------------------------------------------------------------------------------------------
	// Constructors:
	/**
	 * Constructs a new Metadata instance and decodes the metadata file to populate
	 * the Metadata object with relevant information.
	 *
	 * @param metadataFile the metadata file to be decoded and used to initialize the Metadata object
	 */
	public Metadata(File metadataFile) {
		this.metadataFile = metadataFile;
		MetadataDecoder.decode(metadataFile, this);
	}
	
	// ------------------------------------------------------------------------------------------------
	// Methods:
	/**
	 * Encodes the metadata of the current instance using the MetadataEncoder utility.
	 * This method prepares and writes the metadata content to the associated metadata file.
	 *
	 * @return true if the encoding process succeeds and writes the metadata to the file;
	 *         false if an error occurs during the encoding process.
	 */
	public boolean encode() {
		return MetadataEncoder.encode(metadataFile, this);
	}
	
	/**
	 * Adds a new RunnableClass instance to the internal list of runnable classes.
	 * The method ensures no duplicate RunnableClass objects are added
	 * by comparing their paths. If duplicates are detected, the addition is skipped.
	 *
	 * @param newRunnableClass the RunnableClass instance to be added. Must not be null.
	 *                         The path of the RunnableClass is used to check for duplicates.
	 * @return true if the RunnableClass instance is successfully added;
	 *         false if a duplicate exists or the addition process fails.
	 */
	public boolean addRunnableClass(RunnableClass newRunnableClass) {
		// Initialize if null
		if (runnableClasses == null) {
			runnableClasses = runnableClassesTemp;
		}
		
		// Check for duplicates
		if (isRunnableClassPresent(newRunnableClass)) {
			return false;
		}
		
		// Use ArrayList for dynamic resizing, was previously a normal array.
		ArrayList <RunnableClass> updatedRunnableClasses = new ArrayList<>(List.of(runnableClasses));
		updatedRunnableClasses.add(newRunnableClass);
		
		// Update array reference
		runnableClasses = updatedRunnableClasses.toArray(runnableClassesTemp);
		
		return true;
	}
	
	/**
	 * Checks if a RunnableClass with the same path as the provided RunnableClass
	 * exists in the current list of runnable classes.
	 *
	 * @param newRunnableClass the RunnableClass instance to check for in the list.
	 *                         The path of this RunnableClass is used for comparison.
	 * @return true if a RunnableClass with the same path exists in the list,
	 *         false otherwise.
	 */
	private boolean isRunnableClassPresent(RunnableClass newRunnableClass) {
		// Iterate through the existing runnableClasses array
		for (RunnableClass existingRunnableClass : runnableClasses) {
			// Check if any existing class has the same path as the newRunnableClass
			if (existingRunnableClass.getPath().equals(newRunnableClass.getPath())) {
				return true;
			}
		}
		return false;
	}
 
	// TODO make this actually remove the class you are trying to delete on windows
    public boolean removeRunnableClass (String runnableClassPath) {
        // Handles null runnableClasses
        if (runnableClasses == null) {
            return true;
        }
        
        // Filter out classes that do not match the given path
        RunnableClass[] remainingClasses = filterRunnableClasses(runnableClassPath);
        
        // If the size remains unchanged, the class was not found
        if (remainingClasses.length == runnableClasses.length) {
            return false;
        }
        
        runnableClasses = remainingClasses;
        return true;
    }
	
    /**
	 * Filters the array of RunnableClass objects to exclude any instance whose path matches
	 * the specified excluded path. The resulting array contains only the RunnableClass
	 * objects with paths that do not equal the excluded path.
	 *
	 * @param excludedPath the path to be excluded from the filtered results. Any RunnableClass
	 *                     instance with a matching path will not be included in the returned array.
	 * @return an array of RunnableClass objects with paths that do not match the specified
	 *         excluded path. Returns an empty array if no classes match the criterion.
	 */
	private RunnableClass[] filterRunnableClasses (String excludedPath) {
		// Convert the array to a Stream for easy filtering
        return Arrays.stream(runnableClasses)
				// Exclude the class with the specified path
                .filter(runnableClass -> ! runnableClass.getPath().equals(excludedPath))
				// Convert the filtered Stream back to an array
                .toArray(RunnableClass[]::new);
    }
	
	/**
	 * Determines whether a RunnableClass with the specified class path exists in the
	 * list of runnable classes and returns it if found.
	 *
	 * @param classPath the class path to search for in the list of runnable classes.
	 *                  This should represent the path of the class (excluding ".java").
	 * @return the RunnableClass object if a match is found in the list, or null
	 *         if no matching RunnableClass exists.
	 */
	public RunnableClass containRunnableClass(String classPath) {
		if (runnableClasses != null) {
			// Iterate through the array of runnable classes
            for (RunnableClass runnableClass : runnableClasses) {
				// Check if the class path matches (with ".java" extension)
                if ((runnableClass.getPath()).equals(classPath + ".java")) {
					// Return the matching RunnableClass object
                    return runnableClass;
                }
            }
		}
		return null;	// Return null if no matching class is found
	}
	
	// ------------------------------------------------------------------------------------------------
	// Getters:
	/**
	 * Retrieves the metadata file associated with this Metadata instance.
	 *
	 * @return the metadata file represented by this instance
	 */
	public File getFile() {
		return metadataFile;
	}
	
	/**
	 * Retrieves the version associated with this Metadata instance.
	 *
	 * @return the version of the metadata as a String
	 */
	public String getVersion() {
		return version;
	}
	
	/**
	 * Retrieves the directory associated with this Metadata instance.
	 *
	 * @return the directory path as a String
	 */
	public String getDirectory() {
		return directory;
	}
	
	/**
	 * Retrieves the source path associated with this Metadata instance.
	 *
	 * @return the source path as a String
	 */
	public String getSourcePath() {
		return sourcePath;
	}
	
	/**
	 * Retrieves the JRE version associated with this Metadata instance.
	 *
	 * @return the JRE version as a String
	 */
	public String getJREVersion() {
		return JREVersion;
	}
	
	/**
	 * Retrieves the array of RunnableClass objects associated with this Metadata instance.
	 *
	 * @return an array of RunnableClass objects representing the runnable classes contained
	 *         in this Metadata instance. Returns an empty array if no runnable classes are defined.
	 */
	public RunnableClass[] getRunnableClasses() {
		return runnableClasses;
	}
	
	/**
	 * Retrieves the array of internal libraries associated with this Metadata instance.
	 *
	 * @return an array of Strings representing the internal libraries. Returns an empty array
	 *         if no internal libraries are defined.
	 */
	public String[] getInternalLibraries() {
		return internalLibraries;
	}
	
	/**
	 * Retrieves the array of external libraries associated with this Metadata instance.
	 *
	 * @return an array of Strings representing the external libraries. Returns an empty array
	 *         if no external libraries are defined.
	 */
	public String[] getExternalLibraries() {
		return externalLibraries;
	}
	
	// ------------------------------------------------------------------------------------------------
	// Setters:
	/**
	 * Sets the metadata file associated with this Metadata instance.
	 *
	 * @param metadataFile the file to be assigned as the metadata file for this instance
	 */
	public void setFile(File metadataFile) {
		this.metadataFile = metadataFile;
	}
	
	/**
	 * Sets the version string associated with this Metadata instance.
	 *
	 * @param version the version string to assign to this Metadata instance
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
	/**
	 * Sets the directory path associated with this Metadata instance.
	 *
	 * @param directory the directory path to be assigned to this Metadata instance
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	
	/**
	 * Sets the source path associated with this Metadata instance.
	 *
	 * @param sourcePath the source path to set for this Metadata instance
	 */
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	/**
	 * Sets the JRE version associated with this Metadata instance.
	 *
	 * @param jREVersion the JRE version to be assigned to this Metadata instance
	 */
	public void setJREVersion(String jREVersion) {
		JREVersion = jREVersion;
	}
	
	/**
	 * Sets the array of RunnableClass objects associated with this Metadata instance.
	 * This method allows updating the set of runnable classes contained within the metadata.
	 *
	 * @param runnableClasses an array of RunnableClass objects to be assigned to this Metadata instance.
	 *                        Each RunnableClass represents a specific runnable entity defined in the metadata.
	 */
	public void setRunnableClasses(RunnableClass[] runnableClasses) {
		this.runnableClasses = runnableClasses;
	}
	
	/**
	 * Sets the array of internal libraries associated with this Metadata instance.
	 * This method allows defining or updating the internal libraries for the metadata.
	 *
	 * @param internalLibraries an array of Strings representing the internal libraries
	 *                          to associate with this Metadata instance
	 */
	public void setInternalLibraries(String[] internalLibraries) {
		this.internalLibraries = internalLibraries;
	}

	/**
	 * Sets the array of external libraries associated with this Metadata instance.
	 * This method allows defining or updating the external libraries for the metadata.
	 *
	 * @param externalLibraries an array of Strings representing the external libraries
	 *                          to associate with this Metadata instance
	 */
	public void setExternalLibraries(String[] externalLibraries) {
		this.externalLibraries = externalLibraries;
	}
	
	// ------------------------------------------------------------------------------------------------
	// toString:
	/**
	 * Generates a string representation of the Metadata object.
	 * The resulting string includes the version, directory, source path, JRE version,
	 * and details about runnable classes, internal libraries, and external libraries.
	 *
	 * @return a string containing the Metadata object's information in a formatted structure
	 */
	public String toString() {
		String newLineCharacter = "\n";
		
		StringBuilder stringBuilder = new StringBuilder(version + newLineCharacter + directory + newLineCharacter +
                sourcePath + newLineCharacter + JREVersion + newLineCharacter);
		
		return externalLibrariesCheck(
				internalLibrariesCheck(
						runnableClassCheck(stringBuilder)))
				.toString();
		
	}
	
	/**
	 * Appends the string representation of each RunnableClass in the list of runnable classes
	 * to the provided StringBuilder instance. Each RunnableClass is appended followed by a newline.
	 * If the list of runnable classes is null, no modifications are made to the StringBuilder.
	 *
	 * @param stringBuilder the StringBuilder instance to which the RunnableClass details
	 *                      will be appended. Must not be null.
	 * @return the updated StringBuilder instance containing the appended RunnableClass details,
	 *         or the unmodified StringBuilder if the list of runnable classes is null.
	 */
	public StringBuilder runnableClassCheck(StringBuilder stringBuilder) {
		if (runnableClasses != null) {
			for (RunnableClass rc : runnableClasses) {
				stringBuilder.append(rc).append("\n");
			}
		}
		return stringBuilder;
	}
	
	/**
	 * Appends the string representation of each internal library contained in the
	 * `internalLibraries` field to the provided StringBuilder instance. Each library
	 * is appended followed by a newline character. If the `internalLibraries` field
	 * is null, no modifications are made to the StringBuilder.
	 *
	 * @param stringBuilder the StringBuilder instance to which the internal library details
	 *                      will be appended. Must not be null.
	 * @return the updated StringBuilder instance containing the appended internal libraries,
	 *         or the unmodified StringBuilder if the `internalLibraries` field is null.
	 */
	public StringBuilder internalLibrariesCheck(StringBuilder stringBuilder) {
		if (internalLibraries != null) {
			for (String il : internalLibraries) {
				stringBuilder.append(il).append("\n");
			}
		}
		return stringBuilder;
	}
	
	/**
	 * Appends the string representation of each external library contained in the
	 * `externalLibraries` field to the provided StringBuilder instance. Each library
	 * is appended followed by a newline character. If the `externalLibraries` field
	 * is null, no modifications are made to the StringBuilder.
	 *
	 * @param stringBuilder the StringBuilder instance to which the external library details*/
	public StringBuilder externalLibrariesCheck(StringBuilder stringBuilder) {
		if (externalLibraries != null) {
			for (String el : externalLibraries) {
				stringBuilder.append(el).append("\n");
			}
		}
		return stringBuilder;
	}
}