package main.java.zenit.filesystem;

/**
 * The FileHandler class provides utility methods for handling file-related operations.
 * This class serves as a base class for more specific implementations for file handling tasks.
 * It provides functionality to set and manage text encoding used during file processing.
 * <p>
 * Note: This class contains static fields and methods, and is designed for use without instance creation.
 * </p>
 */
public class FileHandler {
	//TODO should this be a class or an interface? or removed entirely?
	
	public final static String UTF = "UTF-8";
	protected static String textEncoding = UTF; //Text-encoding

	public static void setTextEncoding(String encoding) {
		textEncoding = encoding;
	}

}
