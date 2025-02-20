package main.java.zenit.filesystem;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

import main.java.zenit.filesystem.jreversions.JDKDirectories;
import main.java.zenit.filesystem.metadata.Metadata;

/**
 * The MetadataFileHandler class extends FileHandler and provides utility methods for managing
 * metadata files associated with projects. It includes functionality for creating, updating,
 * and modifying metadata files used in the project structure.
 */
public class MetadataFileHandler extends FileHandler {
	//TODO: Update this version number when a new version is released
	public final static String LATEST_VERSION = "2.2.1";

	protected static File createMetadataFile(File projectFile) throws IOException {
		File metadataFile = new File(projectFile.getPath() + File.separator + ".metadata");
		boolean success = metadataFile.createNewFile();
		
		if (!success) {
			throw new IOException("File already exist");
		}
		
		Metadata metadata = new Metadata(metadataFile);
		metadata.setVersion(LATEST_VERSION);
		metadata.setDirectory("bin");
		metadata.setSourcePath("src");
		File JDK = JDKDirectories.getDefaultJDKFile();
		if (JDK != null) {
			metadata.setJREVersion(JDK.getPath());
		}
		metadata.setRunnableClasses(null);
		metadata.setInternalLibraries(null);
		metadata.setExternalLibraries(null);
		
		metadata.encode();
		
		return metadataFile;
	}
	
	protected static Metadata updateMetadata(File metadataFile) {
		Metadata metadata = new Metadata(metadataFile);
		
		if (metadata.getVersion() == null || !metadata.getVersion().equals(LATEST_VERSION)) {
			metadata.setVersion(LATEST_VERSION);
		}
		if (metadata.getDirectory() == null) {
			metadata.setDirectory("bin");
		}
		if (metadata.getSourcePath() == null) {
			metadata.setSourcePath("src");
		}
		if (metadata.getJREVersion() == null || metadata.getJREVersion().equals("unknown")) {
			String JRE = JDKDirectories.getDefaultJDKFile().getPath();
			metadata.setJREVersion(JRE);
		}
		if (metadata.getRunnableClasses() == null) {
			metadata.setRunnableClasses(null);
		}
		if (metadata.getInternalLibraries() == null) {
			metadata.setInternalLibraries(null);
		}
		if (metadata.getExternalLibraries() == null) {
			metadata.setExternalLibraries(null);
		}
		
		if (metadata.encode()) {
			return metadata;
		} else {
			return null;
		}		
	}

	protected static String changeDirectory(File directory, ProjectFile projectFile,
			boolean internal) {
		String directoryPath = directory.getPath();
		
		if (internal) {
			directoryPath = directoryPath.replaceFirst(Matcher.quoteReplacement(
					projectFile.getPath() + File.separator), "");
		}
		
		Metadata metadata = new Metadata(projectFile.getMetadata());
		metadata.setDirectory(directoryPath);
		metadata.encode();
		
		return directoryPath;
	}

	protected static String changeSourcepath(File directory, ProjectFile projectFile,
			boolean internal) {
		String sourcepath = directory.getPath();
		
		if (internal) {
			sourcepath = sourcepath.replaceFirst(Matcher.quoteReplacement(
					projectFile.getPath() + File.separator), "");
		}
		
		Metadata metadata = new Metadata(projectFile.getMetadata());
		metadata.setSourcePath(sourcepath);
		metadata.encode();
		
		return sourcepath;
	}
}
