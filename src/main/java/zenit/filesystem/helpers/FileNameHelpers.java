package main.java.zenit.filesystem.helpers;

import java.io.File;

/**
 * Utility class for handling file paths and extracting relevant components such as
 * project name, package name, and class name. It also provides methods for 
 * manipulating file paths, such as removing parts of the path or renaming folders.
 */
public class FileNameHelpers {
	
	public static String getProjectNameFromFile(File file) {
		String projectName = null;
	
		if (file != null) {
			String[] folders = getFoldersAsStringArray(file);
			
			int srcIndex = getSrcFolderIndex(folders);

			if (srcIndex != -1) {
				projectName = folders[srcIndex - 1]; // Project folder is one step up from src-folder
			} else {
				projectName = folders[folders.length-1];
			}
		}
		
		return projectName;
	}
	
	public static String getPackageNameFromFile(File file) {
		String packageName = null;
		
		if (file != null) {

			String[] folders = getFoldersAsStringArray(file);
			
			int srcIndex = getSrcFolderIndex(folders);

			if (srcIndex != -1 && folders.length > srcIndex) { //Filepath is deeper that src-folder
				packageName = folders[srcIndex + 1]; //Package folder is one step down from src-folder
			}
		}
		
		return packageName;
	}
	
	public static String getClassNameFromFile(File file) {
		String className = null;
		
		if (file != null) {
			String[] folders = getFoldersAsStringArray(file);
			
			int srcIndex = getSrcFolderIndex(folders);
			
			if (srcIndex != -1 && folders.length > srcIndex+2 ) { // Filepath is at least two folders deeper than src-folder
				className = folders[srcIndex +2]; // Class-file is two steps down from src-folder
			}
		}
		return className;
	}
	
	public static File getFilepathWithoutTopFile(File filepath) {
		File newFilepath;
		
		String[] folders = getFoldersAsStringArray(filepath);
		String newFilepathString = "";
		
		for (int index = 0; index < folders.length-1; index++) {
			newFilepathString += folders[index] + "/";
		}
		
		newFilepath = new File(newFilepathString);
		
		return newFilepath;
	}
	
	public static File getFilepathWithoutPackageName(File file) {
		File newFilepath;
		
		String[] folders = getFoldersAsStringArray(file);
		int srcIndex = getSrcFolderIndex(folders);
		String newFilepathString = "";
		
		for (int index = 0; index <= srcIndex; index++) {
			newFilepathString += folders[index] + "/";
		}
		
		newFilepath = new File(newFilepathString);
		
		return newFilepath;
	}
	
	public static File getProjectFilepath(File filepath) {
		String[] folders = getFoldersAsStringArray(filepath);
		int srcIndex = getSrcFolderIndex(folders);
		
		String newFilepath = "";
		for (int index = 0; index < srcIndex; index++) {
			newFilepath += folders[index] + "/";
		}
		
		System.out.println(newFilepath);
		return new File(newFilepath);
	}
	
	public static File renameFolderInFile(File file, String oldName, String newName) {
		String[] folders = getFoldersAsStringArray(file);
		String newFilepath ="";
		
		for (String folder : folders) {
			if (folder.equals(oldName)) {
				folder = newName;
			}
			newFilepath += folder + "/";
		}
		
		return new File(newFilepath);
		
	}

	public static int getSrcFolderIndex(String[] folders) {
		int srcIndex = -1; // Indicates how deep in the file structure the src-folder is
		int counter = 0;
		
		for (String folder : folders) {
			if (folder.equals("src")) {
				srcIndex = counter;
				break;
			}
			counter++;
		}
		return srcIndex;
	}

	public static String[] getFoldersAsStringArray(File file) {
		String[] folders;
		String filepath = file.getAbsolutePath(); // Get the path in string
		folders = filepath.split("/"); // Split path into the different folders
		
		return folders;
	}
}
