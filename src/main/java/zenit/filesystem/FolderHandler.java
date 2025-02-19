package main.java.zenit.filesystem;

import java.io.File;
import java.io.IOException;

import main.java.zenit.filesystem.helpers.FileNameHelpers;

public class FolderHandler {
	
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