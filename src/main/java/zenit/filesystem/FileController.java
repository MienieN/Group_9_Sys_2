package main.java.zenit.filesystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import main.java.zenit.filesystem.helpers.CodeSnippets;
import main.java.zenit.filesystem.metadata.Metadata;

/**
 * The FileController class provides APIs to manage files and directories within
 * a specified workspace. It includes functionalities such as creating, reading,
 * writing, renaming, deleting files, and managing projects and packages.
 * <p>
 * This class also handles the modification of internal and external library dependencies
 * for project files and updates associated metadata.
 * </p>
 */
public class FileController {
	private File workspace; //Used as a base-file for all files
	
	/**
	 * Constructs a FileController instance with the specified workspace directory.
	 *
	 * @param workspace the File object representing the workspace directory
	 */
	public FileController(File workspace) {
		this.workspace = workspace;
	}
	
	/**
	 * Returns the current workspace directory used by the FileController.
	 *
	 * @return the File object representing the current workspace directory
	 */
	public File getWorkspace() {
		return workspace;
	}

	public File createFile(File file, String content, int typeCode) {
		if (file != null) {
			try {
				return JavaFileHandler.createFile(file, content, typeCode);
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
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
	public File createFile(File file, int typeCode) {
		return createFile(file, null, typeCode);
	}
	
	/**
	 * Creates a new file with the specified content and an empty type code.
	 *
	 * @param file the File object representing the file to be created
	 * @param content the content to be written into the created file
	 * @return the created File object or null if file creation fails
	 */
	public File createFile(File file, String content) {
		return createFile(file, content, CodeSnippets.EMPTY);
	}

	public String readFileContent(File file) {
		if (file != null) {
			try {
				return JavaFileHandler.readFile(file);
			} catch (IOException ex) {
				System.err.println("FileController.readFileContent: " + ex.getMessage());
			}
		}
		return null;
	}
	
	public static String readFile(File file) {
		if (file == null) {
			return "";
		}
		
		try (
			var fileReader = new FileReader(file);
			var bufferedReader = new BufferedReader(fileReader);
		) {
			StringBuilder builder = new StringBuilder();

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				builder.append(line);
				builder.append(System.lineSeparator());
			}

			return builder.toString();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();

			// TODO: give the user feedback that the file could not be found
		} catch (IOException ex) {
			ex.printStackTrace();

			// TODO: handle IO exception
		}
		return null;
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

	/**
	 * Creates a package using the provided file parameter.
	 * The method attempts to invoke the `createPackage` functionality
	 * of the `PackageHandler` class. If the operation fails due to an IOException,
	 * an error is logged to the standard error stream, and false is returned.
	 *
	 * @param file the File object representing the directory to be created as a package
	 * @return true if the package creation is successful, false otherwise
	 */
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
	 * Changes the current workspace to the specified directory.
	 * The workspace is updated if the specified directory is successfully set up as a workspace.
	 *
	 * @param workspace the File object representing the new workspace directory
	 * @return true if the workspace was successfully changed, false otherwise
	 */
	public boolean changeWorkspace(File workspace) {
		boolean success = WorkspaceHandler.createWorkspace(workspace);
		if (success) {
			this.workspace = workspace;
		}
		return success;
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
	
	/**
	 * Adds internal library files to the specified project file.
	 * This method uses the ProjectHandler to integrate the libraries into the project.
	 * If an IOException occurs during the operation, the method returns false.
	 *
	 * @param internalLibraryFiles a list of File objects representing the internal library files to be added
	 * @param projectFile the ProjectFile instance representing the project to which the libraries will be added
	 * @return true if the libraries were successfully added, false otherwise
	 */
	public boolean addInternalLibraries(List<File> internalLibraryFiles, ProjectFile projectFile) {
		try {
			return ProjectHandler.addInternalLibraries(internalLibraryFiles, projectFile);
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Removes internal library paths from a specified project file.
	 * This method utilizes the ProjectHandler to perform the removal of libraries.
	 *
	 * @param internalLibraryPaths a list of strings representing the paths of internal libraries to be removed
	 * @param projectFile the ProjectFile instance representing the project from which the libraries will be removed
	 * @return true if the libraries were successfully removed, false otherwise
	 */
	public boolean removeInternalLibraries(List<String> internalLibraryPaths, ProjectFile projectFile) {
		return ProjectHandler.removeInternalLibraries(internalLibraryPaths, projectFile);
	}

	/**
	 * Adds external library files to the specified project file.
	 * This method uses the ProjectHandler to integrate the libraries into the project.
	 *
	 * @param externalLibraryFiles a list of File objects representing the external library files to be added
	 * @param projectFile the ProjectFile instance representing the project to which the libraries will be added
	 * @return true if the libraries were successfully added, false otherwise
	 */
	public boolean addExternalLibraries(List<File> externalLibraryFiles, ProjectFile projectFile) {
		return ProjectHandler.addExternalLibraries(externalLibraryFiles, projectFile);
	}
	
	/**
	 * Removes external library paths from the specified project file.
	 * This method utilizes the ProjectHandler to handle the removal
	 * of the specified external libraries.
	 *
	 * @param externalLibraryPaths a list of strings representing the paths of external libraries
	 *                              to be removed from the project
	 * @param projectFile the ProjectFile instance representing the project
	 *                    from which the external libraries will be removed
	 * @return true if the external libraries were successfully removed, false otherwise
	 */
	public boolean removeExternalLibraries(List<String> externalLibraryPaths,
			ProjectFile projectFile) {
		return ProjectHandler.removeExternalLibraries(externalLibraryPaths, projectFile);
	}
	
	/**
	 * Updates the metadata associated with a given file.
	 *
	 * @param metadataFile the File object representing the metadata file to be updated
	 * @return the updated Metadata object after applying changes from the metadata file
	 */
	public Metadata updateMetadata(File metadataFile) {
		return MetadataFileHandler.updateMetadata(metadataFile);
	}
	
	/**
	 * Changes the directory of the specified project file.
	 * This method allows updating the directory of a given project
	 * file with the option to specify whether the change is internal.
	 * Delegates the operation to the MetadataFileHandler.
	 *
	 * @param directory the File object representing the new directory
	 * @param projectFile the ProjectFile instance representing the project to be updated
	 * @param internal a boolean indicating if the change is internal
	 * @return a String message indicating the result or status of the directory change
	 */
	public String changeDirectory(File directory, ProjectFile projectFile, boolean internal) {
		return MetadataFileHandler.changeDirectory(directory, projectFile, internal);
	}

	/**
	 * Changes the source path of a specified project file to a new directory.
	 * Delegates the operation to the MetadataFileHandler to process the change.
	 *
	 * @param directory the File object representing the new source directory
	 * @param projectFile the ProjectFile instance representing the project for which the source path will be changed
	 * @param internal a boolean indicating whether the source path change is internal
	 * @return a String message indicating the result or status of the source path change
	 */
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
