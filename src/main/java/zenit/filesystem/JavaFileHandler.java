package main.java.zenit.filesystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import main.java.zenit.exceptions.TypeCodeException;
import main.java.zenit.filesystem.helpers.CodeSnippets;
import main.java.zenit.filesystem.helpers.FileNameHelpers;

/**
 * The JavaFileHandler class extends the FileHandler class and provides specific utility methods
 * for creating, reading, saving, renaming, and deleting Java files. This class ensures appropriate
 * handling of files with a ".java" extension and supports encoding settings inherited from FileHandler.
 * It includes additional functionality for generating default Java code snippets when creating files.
 */
public class JavaFileHandler extends FileHandler {

	protected static File createFile(File file, String content, int typeCode) throws IOException {
		try {
			String fileName = file.getName();
			//Adds .java if not already added
			if (!fileName.endsWith(".java")) {
				String filepath = file.getPath();
				filepath += ".java";
				file = new File(filepath);
			}
			//Checks if file already exists
			if (file.exists()) {
				throw new IOException("File already exists");
			}
			//Tries to create file
			file.createNewFile();
			
			//Write content to file
			if (content == null) {
				try {
					content = CodeSnippets.newSnippet(typeCode, file.getName(), FileNameHelpers.getPackageNameFromFile(file));
				} catch (TypeCodeException ex) {
					ex.printStackTrace();
				}
			}
			
			try (BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), textEncoding))) {
				br.write(content);
				br.flush();
			}

			return file;
		} catch (IOException e) {
			throw new IOException("Couldn't create new class");
		}
	}
	
	protected static String readFile(File file) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), textEncoding))) {
			
			String currentString = br.readLine();
			String stringBuilder = "";

			while (currentString != null) {
				stringBuilder += currentString + "\n";
				currentString = br.readLine();
			}
			return stringBuilder;
			
		} catch (IOException ex) {
			throw new IOException("File couldn't be read");
		}
	}
	
	protected static void saveFile(File file, String content) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), textEncoding))) {
			writer.write(content);
			writer.flush();
		}
		catch (IOException ex) {
			throw new IOException(ex.getMessage());
		}
	}

	protected static void failedToDeleteFile(File file) throws IOException {
		if (!file.delete()) {
			throw new IOException("Failed to delete " + file);
		}
	}
}