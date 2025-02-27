package main.java.zenit.filesystem.metadata;

import java.io.File;

import main.java.zenit.filesystem.MetadataFileHandler;

/**
 * The MetadataVerifier class provides functionality to verify the validity
 * and consistency of a Metadata object. Verification involves checking
 * whether the associated metadata file exists and whether the metadata
 * version matches the latest expected version.
 */
public class MetadataVerifier {
	// variables to store the verification status
	public static final int VERIFIED = 0;
	public static final int METADATA_FILE_MISSING = 1;
	public static final int METADATA_OUTDATED = 2;
	
	// -----------------------------------------------------------------------
	// methods:
	public static int verify(Metadata metadata) {
		
		if (metadataFileMissing(metadata)) {
			return METADATA_FILE_MISSING;
		}
		if (metadataOutdated(metadata)) {
			return METADATA_OUTDATED;
		}
		
		return VERIFIED;	
	}
	
	/**
	 * Checks if the metadata file associated with the given Metadata object
	 * is missing, does not exist, or is a directory.
	 *
	 * @param metadata The Metadata object containing the reference to the metadata file.
	 * @return true if the metadata file is missing, does not exist, or is a directory; false otherwise.
	 */
	private static boolean metadataFileMissing(Metadata metadata) {
		File metadataFile = null;
		
		if (metadata != null) {
			metadataFile = metadata.getFile();
		}
        return metadataFile == null || !metadataFile.exists() || metadataFile.isDirectory();
	}

	/**
	 * Determines if the version of the specified Metadata object is outdated.
	 * A metadata object is considered outdated if its version is either null
	 * or does not match the latest expected version.
	 *
	 * @param metadata The Metadata object to check for version compatibility.
	 * @return true if the metadata version is null or does not match the latest version; false otherwise.
	 */
	private static boolean metadataOutdated(Metadata metadata) {
		String version = metadata.getVersion();
        return version == null || ! version.equals(MetadataFileHandler.LATEST_VERSION);
	}
}
