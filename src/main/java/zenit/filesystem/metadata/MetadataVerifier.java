package main.java.zenit.filesystem.metadata;

import java.io.File;

import main.java.zenit.filesystem.MetadataFileHandler;

public class MetadataVerifier {
	
	public static final int VERIFIED = 0;
	public static final int METADATA_FILE_MISSING = 1;
	public static final int METADATA_OUTDATED = 2;
	

	public static int verify(Metadata metadata) {
		
		if (metadataFileMissing(metadata)) {
			return METADATA_FILE_MISSING;
		}
		if (metadataOutdated(metadata)) {
			return METADATA_OUTDATED;
		}
		
		return VERIFIED;	
	}
	
	private static boolean metadataFileMissing(Metadata metadata) {
		File metadataFile = null;
		if (metadata != null) {
			metadataFile = metadata.getMetadataFile();
		}
		if (metadataFile == null || !metadataFile.exists() || metadataFile.isDirectory()) {
			return true;
		}
		
		return false;	
	}

	private static boolean metadataOutdated(Metadata metadata) {
		String version = metadata.getVersion();
		
		if (version == null || !version.equals(MetadataFileHandler.LATEST_VERSION)) {
			return true;
		} else {
			return false;
		}
	}
}
