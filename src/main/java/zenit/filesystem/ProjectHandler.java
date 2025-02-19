package main.java.zenit.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;

import main.java.zenit.filesystem.metadata.Metadata;

public class ProjectHandler extends FolderHandler {
	
	protected static void createNewProject(File file) throws IOException {
		
		boolean success = file.mkdir();
		
		if (success) {
			ProjectFile projectFile = new ProjectFile(file);
			projectFile.addSrc();
			projectFile.addBin();
			
			File metadata = MetadataFileHandler.createMetadataFile(file);
			projectFile.setMetadata(metadata);
			

		} else {
			throw new IOException("Couldn't create project");
		}
	}

	protected static File importProject(File source, File target) throws IOException {
		
		String targetFilepath = target.getPath();
		String projectName = source.getName();
		
		targetFilepath += File.separator + projectName;
		target = new File(targetFilepath);
		
		if (target.exists() ) {
			throw new IOException("A project with that name already exists");
		}
		boolean success = target.mkdir();
		
		if (success) {
			copyFolder(source, target);
			File[] files = target.listFiles();
			boolean metadataMissing = true;
			for (File file : files) {
				if (file.getName().equals(".metadata")) {
					metadataMissing = false;
				}
			}
			if (metadataMissing) {
				MetadataFileHandler.createMetadataFile(target);
			}
			return target;
		} else {
			throw new IOException("Couldn't copy file");
		}
	}

	private static void copyFolder(File sourceFolder, File destinationFolder) throws IOException {
        //Check if sourceFolder is a directory or file
        //If sourceFolder is file; then copy the file directly to new location
        if (sourceFolder.isDirectory()) {
            //Verify if destinationFolder is already present; If not then create it
            if (!destinationFolder.exists()) {
                destinationFolder.mkdir();
            }
             
            //Get all files from source directory
            String files[] = sourceFolder.list();
             
            //Iterate over all files and copy them to destinationFolder one by one
            for (String file : files) {
                File srcFile = new File(sourceFolder, file);
                File destFile = new File(destinationFolder, file);
                 
                //Recursive function call
                copyFolder(srcFile, destFile);
            }
        } else {
            //Copy the file content from one place to another
            Files.copy(sourceFolder.toPath(), destinationFolder.toPath());
        }
    }

	protected static boolean addInternalLibraries(List<File> libraryFiles, ProjectFile projectFile)
			throws IOException {
		//Setup variables
		String[] internalLibraries = new String[libraryFiles.size()];
		int counter = 0;
		File libFolder = projectFile.addLib();
		File destinationFile;
		String libraryName;
		String internalLibraryPath;
		
		//Create internal file path and add to array
		for (File libraryFile : libraryFiles) {
			libraryName = libraryFile.getName();
			destinationFile = new File(libFolder + File.separator + libraryName);
			
			Files.copy(libraryFile.toPath(), destinationFile.toPath());
			
			internalLibraryPath = destinationFile.getPath();
			internalLibraryPath = internalLibraryPath.replaceFirst(Matcher.quoteReplacement(
					projectFile.getPath() + File.separator), "");
			internalLibraries[counter++] = internalLibraryPath;
		}
		
		//Add to metadata
		Metadata metadata = new Metadata(projectFile.getMetadata());
		String[] existingInternalLibraries = metadata.getInternalLibraries();
		
		int eilLength = 0;
		if (existingInternalLibraries != null) {
			eilLength = existingInternalLibraries.length;
		}
		
		String[] newInternalLibraries = new String[eilLength + internalLibraries.length];
		for (int i = 0; i < newInternalLibraries.length; i++) {
			if (i < eilLength) {
				newInternalLibraries[i] = existingInternalLibraries[i];
			} else {
				newInternalLibraries[i] = internalLibraries[i - eilLength];
			}
		}
		
		metadata.setInternalLibraries(newInternalLibraries);
		return metadata.encode();
	}

	protected static boolean removeInternalLibraries(List<String> internalLibraryPathsList, 
			ProjectFile projectFile) {
		//Setup variables
		String[] internalLibraryPaths = new String[internalLibraryPathsList.size()];
		int counter = 0;	
		String libraryPath;
		File library;
		
		//Add full file path and add to array
		for (String internalLibraryPath : internalLibraryPathsList) {
			libraryPath = projectFile.getPath() + File.separator + internalLibraryPath;
			library = new File(libraryPath);
			if (library.delete() == false) {
				return false;
			}
			internalLibraryPaths[counter++] = internalLibraryPath;
		}
		
		//Remove from metadata
		Metadata metadata = new Metadata(projectFile.getMetadata());
		String[] existingInternalLibraries = metadata.getInternalLibraries();
		String[] newInternalLibraries = new String[existingInternalLibraries
		                                           .length-internalLibraryPaths.length];
		counter = 0;
		boolean add = true;
		
		for (int i = 0; i < existingInternalLibraries.length; i++) {
			for (int j = 0; j < internalLibraryPaths.length; j++) {
				if (existingInternalLibraries[i].equals(internalLibraryPaths[j])) {
					add = false;
				}
			}
			if (add) {
				newInternalLibraries[counter++] = existingInternalLibraries[i];
			}
			add = true;
		}
		
		metadata.setInternalLibraries(newInternalLibraries);
		return metadata.encode();
	}

	protected static boolean addExternalLibraries(List<File> externalLibraryFiles, 
			ProjectFile projectFile) {
		//Setup variables
		String[] externalLibraries = new String[externalLibraryFiles.size()];
		int counter = 0;
		
		//Add to array
		for (File externalLibrary : externalLibraryFiles) {
			externalLibraries[counter++] = externalLibrary.getPath();
		}
		
		//Add to metadata
		Metadata metadata = new Metadata(projectFile.getMetadata());
		String[] existingExternalLibraries = metadata.getExternalLibraries();
		
		int eelfLength = 0;
		if (existingExternalLibraries != null) {
			eelfLength = existingExternalLibraries.length;
		}
		
		String[] newExistingExternalLibraries = new String[externalLibraries.length + eelfLength];
		
		for (int i = 0; i < newExistingExternalLibraries.length; i++) {
			if (i < eelfLength) {
				newExistingExternalLibraries[i] = existingExternalLibraries[i];
			} else {
				newExistingExternalLibraries[i] = externalLibraries[i-eelfLength];
			}
		}
		
		metadata.setExternalLibraries(newExistingExternalLibraries);
		return metadata.encode();
	}
	
	protected static boolean removeExternalLibraries(List<String> externalLibraryPathsList, 
			ProjectFile projectFile) {
		//Setup variables
		String[] externalLibraries = new String[externalLibraryPathsList.size()];
		int counter = 0;
		
		//Add to array
		for (String externalLibrary : externalLibraryPathsList) {
			externalLibraries[counter++] = externalLibrary;
		}
		
		//Remove from metadata
		Metadata metadata = new Metadata(projectFile.getMetadata());
		String[] existingExternalLibraries = metadata.getExternalLibraries();
		
		int eelLength = 0;
		if (existingExternalLibraries != null) {
			eelLength = existingExternalLibraries.length;
		}
		
		String[] newExternalLibraries = new String[eelLength-externalLibraries.length];
		counter = 0;
		boolean add = true;
		
		for (int i = 0; i < eelLength; i++) {
			for (int j = 0; j < externalLibraries.length; j++) {
				if (existingExternalLibraries[i].equals(externalLibraries[j])) {
					add = false;
				}
			}
			if (add) {
				newExternalLibraries[counter++] = existingExternalLibraries[i];
			}
			add = true;
		}
		
		metadata.setExternalLibraries(newExternalLibraries);
		return metadata.encode();
	}
}
