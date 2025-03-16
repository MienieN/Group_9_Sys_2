package main.java.zenit.filesystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import main.java.zenit.filesystem.helpers.CodeSnippets;
import main.java.zenit.filesystem.helpers.FileNameHelpers;
import main.java.zenit.filesystem.metadata.Metadata;


public class FileController {
	// variable to store the workspace directory:
	private File workspace; //Used as a base-file for all files
	
	// ------------------------------------------------------------------------------------
	// Constructor:
	/**
	 * Constructs a FileController instance with the specified workspace directory.
	 *
	 * @param workspace the File object representing the workspace directory
	 */
	public FileController(File workspace) {
		this.workspace = workspace;
	}
	
	public FileController() {
	
	}
	
	// ------------------------------------------------------------------------------------
	// File Methods:
	/**
	 * Creates a new file at the specified target location with the provided content and type code.
	 *
	 * @param targetFile the File object representing the target location for the new file
	 * @param fileContent the content to be written into the newly created file
	 * @param typeCode an integer representing the type code of the file, used to determine the file type
	 * @return the created File object if the operation is successful; otherwise, returns null
	 */
	public File createFileAtLocationWithContentAndType (File targetFile, String fileContent, int typeCode) {
		if (targetFile != null) {
			
			try {
				return JavaFileHandler.createFile(targetFile, fileContent, typeCode);
			}
			catch (IOException ex) {
				System.err.println("FileController.createFile operation failed: " + ex.getMessage());
			}
		}
		return null;
	}
	
	/**
	 * Creates a file with a specified type code.
	 *
	 * @param file the File object representing the file to be created
	 * @param typeCode an integer representing the type code of the file
	 * @return the created File object, or null if the creation fails
	 */
	public File createFileWithType (File file, int typeCode) {
		return createFileAtLocationWithContentAndType(file, null, typeCode);
	}
	
	/**
	 * Creates a new file with the specified content and an empty type code.
	 *
	 * @param file the File object representing the file to be created
	 * @param content the content to be written into the created file
	 * @return the created File object or null if file creation fails
	 */
	public File createFileWithContentAndEmptyType (File file, String content) {
		return createFileAtLocationWithContentAndType(file, content, CodeSnippets.EMPTY);
	}
	
	/**
	 * Reads the content of the specified file and returns it as a string.
	 * If the file cannot be read due to an IOException or if the input file
	 * is null, this method returns null.
	 *
	 * @param file the File object representing the file to be read
	 * @return the content of the file as a string if successful;
	 *         otherwise, returns null
	 */
	public String readJavaFileHandlerFileContent (File file) {
		if (file != null) {
			
			try {
				return JavaFileHandler.readFile(file);
			}
			catch (IOException ex) {
				System.err.println("FileController.readFileContent: " + ex.getMessage());
			}
		}
		return null;
	}
	
	/**
	 * Reads the content of the specified file and returns it as a string.
	 * If the file is null, it returns an empty string.
	 * If the file is not found, an error message is logged and an empty string is returned.
	 * If an I/O error occurs while reading the file, an error message is logged and null is returned.
	 *
	 * @param file the File object representing the file to be read
	 * @return the content of the file as a string if successfully read;
	 *         an empty string if the file is null or not found;
	 *         null if an I/O error occurs
	 */
	public static String readFile(File file) {
		if (file == null) {
			return "";
		}
		
		try {
			return readFileContent(file);
		}
		catch (FileNotFoundException e) {
			System.err.println("File not found: " + file.getAbsolutePath() + " " + e.getMessage());
			return ""; // Return empty string if file not found
		}
		catch (IOException e) {
			System.err.println("Error reading file: " + file.getAbsolutePath() + " " + e.getMessage());
			return null; // Signal IO error with null value
		}
	}
	
	/**
	 * Reads the content of the specified file and returns it as a string.
	 * This method reads all lines of the file and appends them to a string
	 * with each line separated by the system's line separator.
	 *
	 * @param file the File object to be read
	 * @return the content of the file as a string
	 * @throws IOException if an I/O error occurs while reading the file
	 */
	private static String readFileContent (File file) throws IOException {
		StringBuilder contentBuilder = new StringBuilder();
		
		try (
				var fileReader = new FileReader(file);
				var bufferedReader = new BufferedReader(fileReader)
		) {
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				contentBuilder.append(line).append(System.lineSeparator());
			}
		}
		return contentBuilder.toString();
	}
	
	/**
	 * Writes the given content to the specified file.
	 * If the file or content is null, or if the write operation fails, the method will return false.
	 *
	 * @param file the File object representing the file to be written to
	 * @param content the content to write into the specified file
	 * @return true if the file is successfully written; false otherwise
	 */
	public boolean writeContentToFile (File file, String content) {
		if (file != null && content != null) {
			
			try {
				JavaFileHandler.saveFile(file, content); //Tries to save file
				return true;
			}
			catch (IOException ex) {
				System.err.println("FileController.writeFile: " + ex.getMessage());
			}
		}
		return false;
	}
	
	/**
	 * Renames a specified file or directory to a new name. The operation distinguishes
	 * between files and directories, utilizing appropriate handlers for each.
	 * This method handles potential exceptions caused by the renaming process.
	 *
	 * @param file the File object representing the file or directory to be renamed
	 * @param newName the new name to assign to the file or directory
	 * @return a File object representing the renamed file or directory if successful;
	 *         otherwise, returns null if the renaming operation fails
	 */
	// TODO this does not work on windows, check how to fix this in the FolderHandler class and JavaFileHandler class
	public File renameFile(File file, String newName) {
		File newFile = null;
		
		if (file != null && newName != null) {
			try {
				if (file.isDirectory()) {
					newFile = FolderHandler.renameFolder(file, newName);
				}
				else {
					newFile = renameFilesAndFolders(file, newName);
				}
			}
			catch (IOException ex) {
				System.err.println("FileController.renameFile: " + ex.getMessage());
			}
		}
		return newFile;
	}
	
	/**
	 * Deletes the specified file or directory. If the file is a directory,
	 * its contents will be deleted recursively. This method logs any
	 * encountered IOException to the error stream.
	 *
	 * @param file the File object representing the file or directory to be deleted
	 * @return true if the file or directory is successfully deleted; false otherwise
	 * @throws NullPointerException if the provided file is null
	 */
	// TODO this does not work on windows, check how to fix this in the FolderHandler class and JavaFileHandler class
	public boolean deleteFile(File file) {
		boolean success = false;
		if (file != null) {
			try {
				if (file.isDirectory()) {
					FolderHandler.deleteFolder(file);
				} else {
					JavaFileHandler.failedToDeleteFile(file);
				}
				success = true;
			} catch (IOException ex) {
				System.err.println("FileController.deleteFile: " + ex.getMessage());
			}
		}
		return success;
	}
	
	// ------------------------------------------------------------------------------------
	// Package and Project Methods:
	/**
	 * Creates a package directory at the specified file location.
	 * This method utilizes the underlying {@code PackageHandler.createPackage} method to create a directory.
	 *
	 * @param file the File object representing the location where the package
	 *             directory should be created
	 * @return true if the package directory is successfully created;
	 *         false if an IOException occurs
	 */
	public boolean createPackage(File file) {
		try {
			PackageHandler.createPackage(file);
			return true;
		}
		catch (IOException ex) {
			System.err.println("FileController.createPackage: " + ex.getMessage());
			return false;
		}
	}
	
	/**
	 * Creates a new project directory with the specified name. If the project name is valid,
	 * the method attempts to create the directory within the workspace and returns the corresponding
	 * File object. If an IOException occurs during the creation process, an error message is logged
	 * and null is returned.
	 *
	 * @param projectName the name of the project to be created
	 * @return the File object representing the created project directory if successful;
	 *         otherwise, returns null
	 */
	public File createProject(String projectName) {
		if (projectName != null) {
			try {
				File file = new File(workspace + "/" + projectName);
				ProjectHandler.createNewProject(file);
				return file;
			}
			catch (IOException ex) {
				System.err.println("FileController.createProject: IOException: " +
						ex.getMessage());
			}
		}
		return null;
	}
	
	/**
	 * Imports a project from the specified source directory into the current workspace.
	 *
	 * @param source the File object representing the source directory of the project to be imported
	 * @return the File object representing the imported project in the workspace
	 * @throws IOException if an I/O error occurs during the import process
	 */
	
	public File importProject(File source) throws IOException {
		return ProjectHandler.importProject(source, workspace);
	}
	
	// ------------------------------------------------------------------------------------
	// Workspace Methods:
	/**
	 * Changes the current workspace to the specified directory. This method
	 * utilizes the WorkspaceHandler to create and set up the new workspace. If
	 * the operation is successful, the workspace is updated internally.
	 *
	 * @param workspace the File object representing the new workspace directory
	 * @return true if the workspace is successfully changed; false otherwise
	 */
	public boolean changeWorkspace(File workspace) {
		boolean success = WorkspaceHandler.createWorkspace(workspace);
		if (success) {
			this.workspace = workspace;
		}
		return success;
	}
	
	/**
	 * Retrieves the current workspace directory.
	 *
	 * @return the File object representing the current workspace directory
	 */
	public File getWorkspace() {
		return workspace;
	}
	
	// ------------------------------------------------------------------------------------
	// Library Methods:
	/**
	 * Adds a list of internal library files to the specified project file. This method
	 * delegates the operation to the ProjectHandler class and handles any IOException
	 * that might occur during the process.
	 *
	 * @param internalLibraryFiles a list of File objects representing the internal library files to be added
	 * @param projectFile the ProjectFile object representing the target project
	 * @return true if the internal libraries are successfully added; false if an IOException occurs
	 */
	public boolean addInternalLibraries(List<File> internalLibraryFiles, ProjectFile projectFile) {
		try {
			return ProjectHandler.addInternalLibraries(internalLibraryFiles, projectFile);
		}
		catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Removes the specified internal libraries from the given project file.
	 * This method delegates the operation to the ProjectHandler class.
	 *
	 * @param internalLibraryPaths a list of strings representing the file paths of the internal libraries to be removed
	 * @param projectFile the ProjectFile object representing the target project from which libraries will be removed
	 * @return true if the internal libraries are successfully removed; false otherwise
	 */
	public boolean removeInternalLibraries(List<String> internalLibraryPaths, ProjectFile projectFile) {
		return ProjectHandler.removeInternalLibraries(internalLibraryPaths, projectFile);
	}
	
	/**
	 * Adds a list of external library files to the specified project file.
	 * This method delegates the operation to the {@code ProjectHandler.addExternalLibraries} method.
	 *
	 * @param externalLibraryFiles a list of {@code File} objects representing the external library files to be added
	 * @param projectFile the {@code ProjectFile} object representing the target project
	 * @return {@code true} if the external libraries are successfully added; {@code false} otherwise
	 */
	public boolean addExternalLibraries(List<File> externalLibraryFiles, ProjectFile projectFile) {
		return ProjectHandler.addExternalLibraries(externalLibraryFiles, projectFile);
	}
	
	/**
	 * Removes the specified external libraries from the given project file by delegating
	 * the operation to the ProjectHandler class.
	 *
	 * @param externalLibraryPaths a list of strings representing the file paths of the
	 * external libraries to be removed
	 * @param projectFile the ProjectFile object representing the target project from
	 * which libraries will be removed
	 * @return true if the external libraries are successfully removed; false otherwise
	 */
	public boolean removeExternalLibraries(List<String> externalLibraryPaths, ProjectFile projectFile) {
		return ProjectHandler.removeExternalLibraries(externalLibraryPaths, projectFile);
	}
	
	// ------------------------------------------------------------------------------------
	// Helper methods:
	/**
	 * Updates the metadata using the provided metadata file.
	 *
	 * @param metadataFile the File object representing the metadata file to update
	 * @return an updated Metadata object
	 */
	public Metadata updateMetadata(File metadataFile) {
		return MetadataFileHandler.updateMetadata(metadataFile);
	}
	
	/**
	 * Changes the directory of the given project file based on the specified parameters.
	 *
	 * @param directory       The target directory to which the project file should be moved or updated.
	 * @param projectFile     The project file whose directory needs to be changed.
	 * @param internal        A boolean flag indicating whether the operation is internal or external.
	 * @return A String representing the status or result of the directory change operation.
	 */
	public String changeDirectory(File directory, ProjectFile projectFile, boolean internal) {
		return MetadataFileHandler.changeDirectory(directory, projectFile, internal);
	}
	
	/**
	 * Modifies the source path for the given project file within the specified directory.
	 *
	 * @param directory the directory in which the operation will be executed
	 * @param projectFile the project file whose source path needs to be changed
	 * @param internal a boolean flag indicating whether the change should be performed internally
	 * @return the updated source path as a string
	 */
	public String changeSourcePath(File directory, ProjectFile projectFile, boolean internal) {
		return MetadataFileHandler.changeSourcepath(directory, projectFile, internal);
	}
	
	/**
	 * Checks if the specified class file contains a main method.
	 *
	 * @param classFile the file object representing the class file to be checked
	 * @return true if the class file contains a method signature for a public static void main, false otherwise
	 */
	public boolean checkIfClassFileContainsMainMethod (File classFile) {
		String content;
		try {
			content = JavaFileHandler.readFile(classFile);
			if (content.contains("public static void main")) {
				return true;
			}
		} catch (IOException e) {
			System.err.println("Error reading file: " + classFile.getPath());
		}
		return false;
	}
	
	/**
	 * Renames a file or folder to a new name while preserving its directory path.
	 *
	 * @param oldFile the original file or folder to be renamed
	 * @param newFilename the new name to assign to the file or folder
	 * @return the newly renamed file or folder as a {@code File} object
	 * @throws IOException if an I/O error occurs, such as if the file already exists or the rename operation fails
	 */
	public static File renameFilesAndFolders (File oldFile, String newFilename) throws IOException {
		File tempFile = FileNameHelpers.getFilepathWithoutTopFile(oldFile); //Removes file name
		
		//Create new file with new name
		String newFilepath = tempFile.getPath() + "/" + newFilename;
		File newFile = new File(newFilepath);
		
		if (newFile.exists()) {
			throw new IOException("File already exists");
		}
		
		boolean success = oldFile.renameTo(newFile);
		
		if (!success) {
			throw new IOException("Couldn't rename file");
		}
		
		return newFile;
	}
}
