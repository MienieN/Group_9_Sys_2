package main.java.zenit.filesystem;

import java.io.File;
import java.io.IOException;

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
	
	/**
	 * Renames the specified folder using the provided new folder name. If the new folder
	 * name is "package", an IOException is thrown indicating that such a name is not allowed.
	 * This method delegates the renaming operation to the FileController class.
	 *
	 * @param file the File object representing the folder to be renamed
	 * @param newFolderName the new name to assign to the folder
	 * @return the File object representing the renamed folder
	 * @throws IOException if the folder cannot be renamed or if the new name is "package"
	 */
	// TODO make this print in the program console or terminal rather than intelliJ terminal
	protected static File renameFolder(File file, String newFolderName) throws IOException {
		if (newFolderName.equals("package")) {
			throw new IOException("Can't rename package to: " + newFolderName);
		}
		
		FileController.renameFilesAndFolders(file, newFolderName);
		
		return file;
	}
	
	/**
	 * Deletes the specified folder and all its contents recursively. If the provided
	 * file is a directory, all its sub-files and subdirectories will be deleted before
	 * the directory itself is removed. If the deletion of any file or directory fails,
	 * an {@code IOException} is thrown.
	 *
	 * @param file the {@code File} object representing the folder or file to be deleted
	 * @throws IOException if a file or directory cannot be deleted
	 */
	// TODO fix this for windows as it does not delete nor does it show the error message
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