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
	
	private File workspace; //Used as a base-file for all files

	
	public FileController(File workspace) {
		this.workspace = workspace;
	}
	
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

	public File createFile(File file, int typeCode) {
		return createFile(file, null, typeCode);
	}
	
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
					JavaFileHandler.deleteFile(file);
				}
				success = true;
			} catch (IOException ex) {
				System.err.println("FileController.deleteFile: " + ex.getMessage());
			}
		}
		return success;
	}

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

	public boolean changeWorkspace(File workspace) {
		boolean success = WorkspaceHandler.createWorkspace(workspace);
		if (success) {
			this.workspace = workspace;
		}
		return success;
	}

	public File importProject(File source) throws IOException {
			File target = ProjectHandler.importProject(source, workspace);
			return target;
	}
	
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
	
	public Metadata updateMetadata(File metadataFile) {
		return MetadataFileHandler.updateMetadata(metadataFile);
	}
	
	public String changeDirectory(File directory, ProjectFile projectFile, boolean internal) {
		return MetadataFileHandler.changeDirectory(directory, projectFile, internal);
	}

	public String changeSourcepath(File directory, ProjectFile projectFile, boolean internal) {
		return MetadataFileHandler.changeSourcepath(directory, projectFile, internal);
	}
	
	public boolean containMainMethod(File classFile) {
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
