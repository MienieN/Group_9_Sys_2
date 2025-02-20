package main.java.zenit.filesystem.metadata;

import java.io.File;
import main.java.zenit.filesystem.RunnableClass;

/**
 * The Metadata class is designed to hold and manage metadata information about a project.
 * This includes details such as the version, directory path, source path, JRE version,
 * runnable classes, as well as internal and external libraries. The Metadata class
 * provides functionality to encode and decode metadata information for persistent storage.
 */
public class Metadata {
	private File metadataFile;
	private String version, directory, sourcePath, JREVersion;
	private RunnableClass[] runnableClasses;
	private String[] internalLibraries, externalLibraries;

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
	 * Retrieves the metadata file associated with this Metadata instance.
	 *
	 * @return the metadata file represented by this instance
	 */
	public File getFile() {
		return metadataFile;
	}

	/**
	 * Sets the metadata file associated with this Metadata instance.
	 *
	 * @param metadataFile the file to be assigned as the metadata file for this instance
	 */
	public void setFile(File metadataFile) {
		this.metadataFile = metadataFile;
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
	 * Sets the version string associated with this Metadata instance.
	 *
	 * @param version the version string to assign to this Metadata instance
	 */
	public void setVersion(String version) {
		this.version = version;
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
	 * Sets the directory path associated with this Metadata instance.
	 *
	 * @param directory the directory path to be assigned to this Metadata instance
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
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
	 * Sets the source path associated with this Metadata instance.
	 *
	 * @param sourcePath the source path to set for this Metadata instance
	 */
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
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
	 * Sets the JRE version associated with this Metadata instance.
	 *
	 * @param jREVersion the JRE version to be assigned to this Metadata instance
	 */
	public void setJREVersion(String jREVersion) {
		JREVersion = jREVersion;
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
	 * Sets the array of RunnableClass objects associated with this Metadata instance.
	 * This method allows updating the set of runnable classes contained within the metadata.
	 *
	 * @param runnableClasses an array of RunnableClass objects to be assigned to this Metadata instance.
	 *                        Each RunnableClass represents a specific runnable entity defined in the metadata.
	 */
	public void setRunnableClasses(RunnableClass[] runnableClasses) {
		this.runnableClasses = runnableClasses;
	}
	
	public boolean addRunnableClass(RunnableClass runnableClass) {
		if (runnableClasses == null) {
			runnableClasses = new RunnableClass[0];
		}
		
		RunnableClass[] temp = new RunnableClass[runnableClasses.length + 1];
		for (int i = 0; i < runnableClasses.length; i++) {
			if (runnableClasses[i].getPath().equals(runnableClass.getPath())) {
				return false;
			} else {
				temp[i] = runnableClasses[i];
			}
		}

		temp[temp.length - 1] = runnableClass;
		runnableClasses = temp;

		return true;
	}
	
	public boolean removeRunnableClass(String runnableClassPath) {
		if (runnableClasses != null) {
			RunnableClass[] temp = new RunnableClass[runnableClasses.length-1];
			int counter = 0;
			for (int i = 0; i < runnableClasses.length; i++) {
				if (!runnableClasses[i].getPath().equals(runnableClassPath)) {
					if (counter != temp.length) {
						temp[counter++] = runnableClasses[i];
					} else {
						return false;
					}
				}
			}
			
			runnableClasses = temp;
		}

		return true;
	}
	
	public RunnableClass containRunnableClass(String classPath) {
		if (runnableClasses != null) {
			for (int i = 0; i < runnableClasses.length; i++) {
				if ((runnableClasses[i].getPath()).equals(classPath + ".java")) {
					return runnableClasses[i];
				}
			}
		}
		return null;
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
	 * Retrieves the array of external libraries associated with this Metadata instance.
	 *
	 * @return an array of Strings representing the external libraries. Returns an empty array
	 *         if no external libraries are defined.
	 */
	public String[] getExternalLibraries() {
		return externalLibraries;
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

	public String toString() {
		String nl = "\n";
		String string = version + nl + directory + nl + sourcePath + nl + JREVersion + nl;
		if (runnableClasses != null) {
			for (RunnableClass rc : runnableClasses) {
				string += rc + nl;
			}
		}
		if (internalLibraries != null) {
			for (String il : internalLibraries) {
				string += il + nl;
			}
		}
		if (externalLibraries != null) {
			for (String el : externalLibraries) {
				string += el + nl;
			}
		}

		return string;
	}
}