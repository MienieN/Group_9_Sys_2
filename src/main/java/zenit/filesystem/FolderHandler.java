package main.java.zenit.filesystem;

import java.io.File;
import java.io.IOException;

import main.java.zenit.filesystem.helpers.FileNameHelpers;

/**
 * The FolderHandler class provides methods for handling folder-related operations.
 * This includes creating new folders, renaming existing folders, and recursively
 * deleting folders and their contents.
 * <p>
 * The methods in this class are protected and static, intended to be utilized by
 * subclasses or other components within the same package that need folder management functionality.
 * </p>
 */
public class FolderHandler {
	
	/**
	 * Creates a new folder at the specified file path.
	 * If the folder cannot be created, an IOException is thrown.
	 *
	 * @param file the File object representing the folder to be created
	 * @throws IOException if the folder could not be created
	 */
	protected static void createNewFolder(File file) throws IOException {
		boolean success = file.mkdirs();
		if (!success) {
			throw new IOException ("Couldn't create folder " + file.getName());
		}
	}
	
	protected static File renameFolder(File file, String newFolderName) throws IOException {
		
		if (newFolderName.equals("package")) {
			throw new IOException("Can't rename package to: " + newFolderName);
		}
		
		File tempFile = FileNameHelpers.getFilepathWithoutTopFile(file); //Removes file name
		
		//Create new file
		String newFilepath = tempFile.getPath() + "/" + newFolderName;
		File newFile = new File(newFilepath);
		
		//Check if file exists
		if (newFile.exists()) {
			throw new IOException("File already exists");
		}
		
		//Rename file
		boolean success = file.renameTo(newFile);
		if (!success) {
			throw new IOException("Couldn't rename file");
		}
	
		return newFile;
	}
	

	protected static void deleteFolder(File file) throws IOException {
		if (file.isDirectory()) {
			File[] entries = file.listFiles();
			if (entries != null) {
				for (File entry : entries) {
					deleteFolder(entry); //Recursively delete files
				}
			}
		}
		if (!file.delete()) {
			throw new IOException("Failed to delete " + file);
		}
	}
}