package main.java.zenit.console;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A custom OutputStream implementation that redirects error output into a
 * {@link ConsoleArea}, allowing error messages to be displayed in a graphical console view.
 */
public class ConsoleAreaErrorStream extends OutputStream {
	private final ConsoleArea consoleArea;
	private final StringBuilder stringBuilder = new StringBuilder();
	private static final int NEWLINE_CHAR = '\n', CARRIAGE_RETURN_CHAR = '\r';
	
	// ------------------------------------------------------------------------------------
	// Constructors:
	/**
	 * Constructs a ConsoleAreaErrorStream that redirects error output to the specified ConsoleArea.
	 *
	 * @param consoleArea the ConsoleArea instance to which the error output will be redirected
	 */
	public ConsoleAreaErrorStream(ConsoleArea consoleArea) {
		this.consoleArea = consoleArea;
	}
	
	// ------------------------------------------------------------------------------------
	// Methods:
	/**
	 * Flushes the current content of the StringBuilder to the associated ConsoleArea as error output.
	 * This method clears the StringBuilder after writing its content to ensure no residual data is retained.
	 * It uses the {@code errPrint} method of the ConsoleArea to display the error message in the console view.
	 */
	@Override
	public void flush() {
		consoleArea.printError(stringBuilder.toString());
		stringBuilder.setLength(0);
	}
	
	/**
	 * Closes the ConsoleAreaErrorStream, releasing any resources associated with it.
	 * This method ensures that the underlying OutputStream is closed properly.
	 */
	@Override
    public void close() {
        try {
            super.close();
        }
        catch (IOException e) {
			System.err.println("Failed to close ConsoleAreaErrorStream: " + e.getMessage());
			e.printStackTrace();
        }
    }
	
    /**
	 * Writes a single byte to the output stream. This method determines if the byte
	 * corresponds to a newline character, a carriage return character, or any
	 * other character, and processes it accordingly.
	 *
	 * @param byteToWrite the byte to write, which is interpreted as an 8-bit value. Non-ASCII values may result in undefined behavior.
	 * @throws IOException if an I/O error occurs while writing the byte.
	 */
	@Override
    public void write(int byteToWrite) throws IOException {
        if (byteToWrite == NEWLINE_CHAR) {
            handleNewLine();
        }
        else if (byteToWrite != CARRIAGE_RETURN_CHAR) {
            appendSingleCharacter((char) byteToWrite);
        }
    }
	
    /**
	 * Processes a new line in the error stream by flushing the current content of the {@code stringBuilder}
	 * to the associated {@code consoleArea} as error output. This method explicitly appends a newline character
	 * to the output and clears the {@code stringBuilder} to prepare for future data.
	 * <p>
	 * The error output is displayed in the {@code consoleArea} with a specific style (e.g., red text color),
	 * and the method leverages the {@code errorPrint} method of the {@code ConsoleArea} to handle the rendering.
	 * </p>
	 */
	private void handleNewLine() {
        consoleArea.printError(stringBuilder.toString() + "\n"); //TODO can we remove the toString()?
        stringBuilder.setLength(0); // Clear the stringBuilder
    }
	
    /**
	 * Appends a single character to the internal StringBuilder and simultaneously outputs it
	 * as error text to the associated console area.
	 *
	 * @param charToAppend the character to be appended and displayed as error output
	 */
	private void appendSingleCharacter(char charToAppend) {
        stringBuilder.append(charToAppend);
        consoleArea.printError(String.valueOf(charToAppend)); // Use String.valueOf for conversion
    }
	
	/**
	 * Writes a sequence of bytes from the specified byte array to the error output stream.
	 * This method uses the provided offset and length to determine the range of bytes to write.
	 * The written content is processed as a string and displayed through the associated ConsoleArea's error printing mechanism.
	 *
	 * @param bytes the byte array containing the data to be written
	 * @param off the starting offset in the byte array from which data is to be written
	 * @param len the number of bytes to write from the byte array
	 * @throws IOException if an I/O error occurs while attempting to write the data
	 */
	@Override
	public void write(byte[] bytes, int off, int len) throws IOException {
		consoleArea.printError(new String(bytes, off, len));
	}
}