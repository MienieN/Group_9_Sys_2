package main.java.zenit.filesystem.helpers;

import java.io.File;

/**
 * Utility class for handling file paths and extracting relevant components such as
 * project name, package name, and class name. It also provides methods for 
 * manipulating file paths, such as removing parts of the path or renaming folders.
 */
public class FileNameHelpers {
	
	// ------------------------------------------------------------------------------------
	// Get project name methods:
    /**
	 * Extracts the project name from the given file by analyzing its folder structure.
	 * If the folder "src" is present in the file path, the project name is assumed
	 * to be the folder located directly above the "src" folder. If no "src" folder
	 * is found, the project name defaults to the last folder in the file path.
	 *
	 * @param file the file from which to extract the project name. Must not be null.
	 * @return the name of the project extracted from the file path, or null if the provided file is null.
	 */
	public static String getProjectNameFromFile (File file) {
        if (file == null) {
            return null;
        }
        
        String[] folderArray = getFoldersAsStringArray(file);
        int srcFolderIndex = getSrcFolderIndex(folderArray);
        
        return (srcFolderIndex != - 1)
                ? getProjectNameFromSrcIndex(folderArray, srcFolderIndex)
                : getProjectNameDefault(folderArray);
    }
	
	/**
	 * Retrieves the project name by analyzing the folder structure from a given array of folder names.
	 * The project name is assumed to be the folder located directly above the "src" folder.
	 *
	 * @param folderArray an array of folder names representing the file path components.
	 *                    Each element corresponds to a folder in the file path, in order.
	 * @param srcFolderIndex the index of the "src" folder within the folderArray.
	 *                       Must be greater than 0, as the project name is expected to be one level above the "src" folder.
	 * @return the name of the project, which is the folder immediately preceding the "src" folder.
	 *         Returns null if the input is invalid or the index is out of bounds.
	 */
	private static String getProjectNameFromSrcIndex (String[] folderArray, int srcFolderIndex) {
		return folderArray[srcFolderIndex - 1]; // Project folder is one step up from the src folder
	}
	
	/**
	 * Determines the default project name based on the provided folder structure.
	 * If no "src" folder is found during the folder analysis, the project name
	 * is assumed to be the name of the last folder in the array.
	 *
	 * @param folderArray an array of folder names representing the file path components.
	 *                    Each element corresponds to a folder in the file path, in order.
	 * @return the default project name derived from the last folder in the folderArray.
	 */
	private static String getProjectNameDefault (String[] folderArray) {
		return folderArray[folderArray.length - 1]; // Default to the last folder if src not found
	}
	
	// ------------------------------------------------------------------------------------
	// Get package name method:
	/**
	 * Extracts the package name from the provided file by analyzing its directory structure.
	 * The method assumes that the package folder is located directly below the "src" folder.
	 * If the "src" folder is not present in the file path, the method returns null.
	 *
	 * @param file the file whose package name is to be determined. Must not be null.
	 * @return the name of the package folder one level below the "src" folder, or null
	 *         if the file is null or the "src" folder is not found in the path.
	 */
	// TODO: this is not working for windows, it is not returning the package name to be added to
	//  the newly created class (see JavaFileHandler.java)
	public static String getPackageNameFromFile (File file) {
		if (file == null) {
			return null;
		}
		
		String[] folders = getFoldersAsStringArray(file);
		int srcIndex = getSrcFolderIndex(folders);
		return getFolderAtDepth(folders, srcIndex, 1); // Package folder is one level below "src"
	}
	
	// ------------------------------------------------------------------------------------
	// Get class name from file method:
	/**
	 * Extracts the class name from the provided file by analyzing its folder structure.
	 * The method assumes that the class folder is located two levels below the "src" folder.
	 * If the file is null, the method returns null.
	 *
	 * @param file the file whose class name is to be determined. Must not be null.
	 * @return the name of the class folder located two levels below the "src" folder, or
	 *         null if the file is null or the "src" folder is not found in the path.
	 */
	public static String getClassNameFromFile (File file) {
		if (file == null) {
			return null;
		}
		
		String[] folders = getFoldersAsStringArray(file);
		int srcIndex = getSrcFolderIndex(folders);
		return getFolderAtDepth(folders, srcIndex, 2); // Class folder is two levels below "src"
	}
	
	// ------------------------------------------------------------------------------------
	// Validation methods:
	/**
	 * Validates whether the specified depth, derived from a starting index and offset,
	 * falls within the bounds of the provided folder array.
	 *
	 * @param folders the array of folder names representing the directory structure.
	 *                Each element corresponds to a folder in the file path, in order.
	 * @param srcIndex the index of the starting folder (e.g., "src") within the array.
	 *                 Must be non-negative and within the bounds of the array.
	 * @param offsetFromSrc the offset to apply to the starting index to calculate the target depth.
	 *                      This can be positive or negative depending on the needed traversal direction.
	 * @return true if the calculated target depth (srcIndex + offsetFromSrc) is within the bounds
	 *         of the folder array; false otherwise.
	 */
	public static boolean isValidDepth(String[] folders, int srcIndex, int offsetFromSrc) {
		return srcIndex != - 1 && folders.length > srcIndex + offsetFromSrc;
	}
	
	/**
	 * Renames a specified folder in the file's path by replacing the old folder name
	 * with a new folder name. The method iterates through the folder structure of the file,
	 * performing the replacement, and returns a new File object with the updated path.
	 *
	 * @param file the original file whose path is to be modified. Must not be null.
	 * @param oldName the name of the folder to be replaced in the file's path. Must not be null.
	 * @param newName the new name to replace the old folder name with. Must not be null.
	 * @return a new File object with the updated path after the folder rename. Returns the
	 *         original file object if no matching folder name is found or if the input is invalid.
	 */
	public static File renameFolderInFile(File file, String oldName, String newName) {
		String[] folders = getFoldersAsStringArray(file);
		StringBuilder newFilepath = new StringBuilder();
		
		for (String folder : folders) {
			if (folder.equals(oldName)) {
				folder = newName;
			}
			newFilepath.append(folder).append("/");
		}
		return new File(newFilepath.toString());
	}
	
	// ------------------------------------------------------------------------------------
	// Getters:
	/**
	 * Splits the absolute path of the given file into an array of folder names.
	 * Each folder in the path is represented as an element in the returned array.
	 *
	 * @param file the file whose absolute path is to be split into folders. Must not be null.
	 * @return an array of strings where each element represents a folder in the file's path.
	 *         Returns an empty array if the file path is empty or invalid.
	 */
	public static String[] getFoldersAsStringArray(File file) {
		String[] folders;
		String filepath = file.getAbsolutePath(); // Get the path in string
		folders = filepath.split("/"); // Split path into the different folders
		
		return folders;
	}
	
	/**
	 * Finds the index of the "src" folder in the given array of folder names.
	 * If the "src" folder is not found, the method returns -1.
	 *
	 * @param folders an array of folder names representing the directory structure.
	 *                Each element corresponds to a folder in order of hierarchy.
	 * @return the index of the first occurrence of "src" in the array, or -1 if "src" is not found.
	 */
	public static int getSrcFolderIndex(String[] folders) {
		int srcIndex = -1; // null check
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
	
	/**
	 * Retrieves a folder name from the specified array at a particular depth,
	 * determined by the starting index and an offset value. If the calculated depth
	 * is invalid or out of bounds, the method returns null.
	 *
	 * @param folders an array of folder names representing the directory structure.
	 *                Each element corresponds to a folder in the file path, in order.
	 * @param srcIndex the index of the starting folder (e.g., "src") within the array.
	 *                 Must be within the bounds of the array.
	 * @param offsetFromSrc the offset to be applied to the starting index to calculate
	 *                      the target depth. Can be positive or negative.
	 * @return the folder name at the calculated depth if valid; null otherwise.
	 */
	public static String getFolderAtDepth(String[] folders, int srcIndex, int offsetFromSrc) {
		return isValidDepth(folders, srcIndex, offsetFromSrc)
				? folders[srcIndex + offsetFromSrc]
				: null;
	}
	
	/**
	 * Constructs a new File object that represents the directory path of the given file
	 * excluding its top-most folder or file. The top directory or file in the provided
	 * path is removed from the resulting file path.
	 *
	 * @param filepath the file object representing the original file path. Must not be null.
	 * @return a new File object representing the file path without its top directory or file.
	 *         Returns an empty File path if the provided path contains only one folder or is invalid.
	 */
	public static File getFilepathWithoutTopFile(File filepath) {
		File newFilepath;
		
		String[] folders = getFoldersAsStringArray(filepath);
		StringBuilder newFilepathString = new StringBuilder();
		
		for (int index = 0; index < folders.length-1; index++) {
			newFilepathString.append(folders[index]).append("/");
		}
		
		newFilepath = new File(newFilepathString.toString());
		
		return newFilepath;
	}
	
	/**
	 * Constructs a new File object representing the directory path up to and including
	 * the "src" folder of the provided file. This excludes folders and files located
	 * beyond the "src" folder in the file path.
	 *
	 * @param file the file whose path is to be trimmed up to the "src" folder. Must not be null.
	 * @return a new File object representing the file path up to the "src" folder, inclusive.
	 *         Returns an empty File path if "src" is not found in the directory structure.
	 */
	public static File getFilepathWithoutPackageName(File file) {
		File newFilepath;
		
		String[] folders = getFoldersAsStringArray(file);
		int srcIndex = getSrcFolderIndex(folders);
		StringBuilder newFilepathString = new StringBuilder();
		
		for (int index = 0; index <= srcIndex; index++) {
			newFilepathString.append(folders[index]).append("/");
		}
		
		newFilepath = new File(newFilepathString.toString());
		
		return newFilepath;
	}
	
	/**
	 * Constructs a new File object representing the project file path by analyzing the given file path.
	 * The project file path is determined by truncating the folder structure
	 * at the directory containing the "src" folder.
	 *
	 * @param filepath the File object representing the original file path. Must not be null.
	 * @return a new File object representing the file path up to the directory containing the "src" folder.
	 *         Returns a File object with an empty path if "src" is not found in the directory structure.
	 */
	public static File getProjectFilepath(File filepath) {
		String[] folders = getFoldersAsStringArray(filepath);
		int srcIndex = getSrcFolderIndex(folders);
		
		StringBuilder newFilepath = new StringBuilder();
		for (int index = 0; index < srcIndex; index++) {
			newFilepath.append(folders[index]).append("/");
		}
		
		System.out.println(newFilepath);
		return new File(newFilepath.toString());
	}
}
