package main.java.zenit.filesystem.helpers;

import java.io.File;

public class FileNameHelpers {
	
	public static String getProjectnameFromFile(File file) {
		String projectname = null;
	
		if (file != null) {
			String[] folders = getFoldersAsStringArray(file);
			
			int srcIndex = getSrcFolderIndex(folders);

			if (srcIndex != -1) {
				projectname = folders[srcIndex - 1]; //Projectfolder is one step up from src-folder
			} else {
				projectname = folders[folders.length-1];
			}
		}
		
		return projectname;
	}
	
	public static String getPackagenameFromFile(File file) {
		String packagename = null;
		
		if (file != null) {

			String[] folders = getFoldersAsStringArray(file);
			
			int srcIndex = getSrcFolderIndex(folders);

			if (srcIndex != -1 && folders.length > srcIndex) { //Filepath is deeper that src-folder
				packagename = folders[srcIndex + 1]; //Package folder is one step down from src-folder
			}
		}
		
		return packagename;
	}
	
	public static String getClassnameFromFile(File file) {
		String classname = null;
		
		if (file != null) {
			String[] folders = getFoldersAsStringArray(file);
			
			int srcIndex = getSrcFolderIndex(folders);
			
			if (srcIndex != -1 && folders.length > srcIndex+2 ) { //Filepath is atleast two folders deeper than src-folder
				classname = folders[srcIndex +2]; //Class-file is two steps down from src-folder
			}
		}
		return classname;
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
		int srcIndex = -1; //Indicates how deep in the filestructure the src-folder is
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
		String filepath = file.getAbsolutePath(); //Get the path in string
		folders = filepath.split("/"); //Split path into the different folders
		
		return folders;
	}
}
