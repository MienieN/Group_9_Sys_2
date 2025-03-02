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

	/**
	 * Creates a new file with the specified content and ensures the file has a ".java" extension.
	 * If the file already exists, an {@code IOException} is thrown. If the provided content is
	 * {@code null}, default content is generated based on the specified type code.
	 *
	 * @param file the {@code File} object representing the file to be created. If the file does not
	 *             have a ".java" extension, it is added automatically.
	 * @param content the {@code String} content to be written to the file. If {@code null}, default
	 *                content is generated using the type code.
	 * @param typeCode an integer representing the type of content to generate if the content is
	 *                 {@code null}. This determines whether the default content represents an empty
	 *                 structure, a class, or an interface.
	 * @return the {@code File} object representing the created file.
	 * @throws IOException if an I/O error occurs or if the file already exists.
	 */
	protected static File createFile(File file, String content, int typeCode) throws IOException {
		try {
			//Adds .java if not already added
			file = ensureJavaExtension(file);
			
			//Checks if file already exists
			if (file.exists()) {
				throw new IOException("File already exists");
			}
			
			//Tries to create file
			file.createNewFile();
			
			//Write content to file
			if (content == null) {
				content = generateDefaultContent(file, typeCode);
			}
			
			try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), textEncoding))) {
				bufferedWriter.write(content);
				bufferedWriter.flush();
			}
			return file;
		} catch (IOException e) {
			throw new IOException("Couldn't create new class");
		}
	}
	
	/**
	 * Ensures that the provided {@code File} object has a ".java" file extension.
	 * If the file does not already have the ".java" extension, a new {@code File} object
	 * with the updated name is returned.
	 *
	 * @param file the {@code File} object to check and adjust for the ".java" extension
	 * @return a {@code File} object with the ".java" extension
	 */
	private static File ensureJavaExtension(File file) {
		String fileName = file.getName();
		
		if (!fileName.endsWith(".java")) {
			String filepath = file.getPath();
			filepath += ".java";
			file = new File(filepath);
		}
		return file;
	}
	
	/**
	 * Generates default content for a given file based on the specified type code.
	 * The method utilizes `CodeSnippets.newSnippet` to generate the content, which
	 * can represent an empty snippet, a class definition, or an interface definition.
	 *
	 * @param file the {@code File} object for which the default content is generated.
	 *             The name of the file is used as part of the snippet generation.
	 * @param typeCode an integer representing the type of snippet to generate. Valid codes include:
	 *                 - `CodeSnippets.EMPTY` for an empty snippet,
	 *                 - `CodeSnippets.CLASS` for a class definition,
	 *                 - `CodeSnippets.INTERFACE` for an interface definition.
	 * @return a {@code String} containing the generated content. If an exception occurs during
	 *         content generation, {@code null} is returned.
	 */
	private static String generateDefaultContent(File file, int typeCode) {
		String content = null;
		try {
			content = CodeSnippets.newSnippet(typeCode, file.getName(), FileNameHelpers.getPackageNameFromFile(file));
		} catch (TypeCodeException ex) {
			ex.printStackTrace();
		}
		return content;
	}
	
	/**
	 * Reads the contents of the specified file and returns it as a single string.
	 * Each line of the file is separated by a newline character in the returned string.
	 *
	 * @param file the {@code File} object representing the file to be read
	 * @return a {@code String} containing the contents of the file
	 * @throws IOException if an I/O error occurs while reading the file
	 */
	protected static String readFile(File file) throws IOException {
		try (BufferedReader bufferedReader = createBufferedReader(file)) {
			
			StringBuilder fileContents = new StringBuilder();
			String lineReader = bufferedReader.readLine();
			String currentLine;

			while ((currentLine = lineReader) != null) {
				fileContents.append(currentLine).append("\n");
			}
			return fileContents.toString();
			
		} catch (IOException ex) {
			throw new IOException("File couldn't be read: " + ex.getMessage());
		}
	}
	
	/**
	 * Creates a buffered reader for the specified file using the configured text encoding.
	 *
	 * @param file the {@code File} object representing the file to be read
	 * @return a {@code BufferedReader} that can be used to read the file
	 * @throws IOException if an I/O error occurs while creating the buffered reader
	 */
	private static BufferedReader createBufferedReader(File file) throws IOException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(file), textEncoding));
	}
	
	/**
	 * Saves the given content to the specified file. The file's content will
	 * be overwritten if it already exists.
	 *
	 * @param file the {@code File} object representing the file to which content will be written
	 * @param content the {@code String} content to be saved in the file
	 * @throws IOException if an I/O error occurs while saving the file
	 */
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

	/**
	 * Attempts to delete the specified file. If the file cannot be deleted,
	 * throws an {@code IOException} with an error message.
	 *
	 * @param file the {@code File} object representing the file to be deleted
	 * @throws IOException if the file cannot be deleted
	 */
	// TODO fix this for windows as it does not delete nor does it show the error message
	protected static void failedToDeleteFile(File file) throws IOException {
		if (!file.delete()) {
			throw new IOException("Failed to delete " + file);
		}
	}
}