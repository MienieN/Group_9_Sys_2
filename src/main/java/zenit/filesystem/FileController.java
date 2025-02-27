package main.java.zenit.filesystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import main.java.zenit.filesystem.helpers.CodeSnippets;
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
	
	public boolean writeFile(File file, String content) {
		
		if (file != null && content != null) {
			try {
				JavaFileHandler.saveFile(file, content); //Tries to save file
				return true;
			} catch (IOException ex) {
				System.err.println("FileController.writeFile: " + ex.getMessage());
			}
		}
		return false;
	}
	
	public File renameFile(File file, String newName) {
		File newFile = null;
		if (file != null && newName != null) {
			try {
				if (file.isDirectory()) {
					newFile = FolderHandler.renameFolder(file, newName);
				} else {
					newFile = JavaFileHandler.renameFile(file, newName);
				}
			} catch (IOException ex) {
				System.err.println("FileController.renameFile: " + ex.getMessage());
			}
		}
		return newFile;
	}
	
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

	public boolean createPackage(File file) {
		try {
			PackageHandler.createPackage(file);
			return true;
		} catch (IOException ex) {
			System.err.println("FileController.createPackage: " + ex.getMessage());
			return false;
		}
	}
	
	public File createProject(String projectname) {
		if (projectname != null) {
			try {
				File file = new File(workspace + "/" + projectname);
				ProjectHandler.createNewProject(file);
				return file;
			} catch (IOException ex) {
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
	
	public boolean changeWorkspace(File workspace) {
		boolean success = WorkspaceHandler.createWorkspace(workspace);
		if (success) {
			this.workspace = workspace;
		}
		return success;
	}
	
	
	public File getWorkspace() {
		return workspace;
	}
	
	// ------------------------------------------------------------------------------------
	// Library Methods:

	public boolean addInternalLibraries(List<File> internalLibraryFiles, ProjectFile projectFile) {
		try {
			return ProjectHandler.addInternalLibraries(internalLibraryFiles, projectFile);
		} catch (IOException e) {
			return false;
		}
	}
	
	
	public boolean removeInternalLibraries(List<String> internalLibraryPaths, ProjectFile projectFile) {
		return ProjectHandler.removeInternalLibraries(internalLibraryPaths, projectFile);
	}
	
	
	public boolean addExternalLibraries(List<File> externalLibraryFiles, ProjectFile projectFile) {
		return ProjectHandler.addExternalLibraries(externalLibraryFiles, projectFile);
	}
	

	public boolean removeExternalLibraries(List<String> externalLibraryPaths,
										   ProjectFile projectFile) {
		return ProjectHandler.removeExternalLibraries(externalLibraryPaths, projectFile);
	}
	
	// ------------------------------------------------------------------------------------
	// Helper methods:
	
	public Metadata updateMetadata(File metadataFile) {
		return MetadataFileHandler.updateMetadata(metadataFile);
	}
	
	
	public String changeDirectory(File directory, ProjectFile projectFile, boolean internal) {
		return MetadataFileHandler.changeDirectory(directory, projectFile, internal);
	}

	
	public String changeSourcePath(File directory, ProjectFile projectFile, boolean internal) {
		return MetadataFileHandler.changeSourcepath(directory, projectFile, internal);
	}
	
	public boolean checkIfClassFileContainsMainMethod (File classFile) {
		String content;
		try {
			content = JavaFileHandler.readFile(classFile);
			if (content.contains("public static void main")) {
				return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
}
