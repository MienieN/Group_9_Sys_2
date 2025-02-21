package main.java.zenit.console;

import java.io.IOException;
import java.io.OutputStream;

/**
 * ConsoleAreaOutputStream is a custom implementation of the OutputStream that directs
 * output to a ConsoleArea object. It captures the output intended for the console and
 * sends textual data for display within the provided ConsoleArea instance.
 *
 * This class ensures that the output written to this stream is formatted and displayed
 * in real-time within the ConsoleArea, preserving the visual characteristics for
 * user interaction in applications.
 */
public class ConsoleAreaOutputStream extends OutputStream {
	private final ConsoleArea consoleArea;
	private final StringBuilder stringBuilder = new StringBuilder();
	
	/**
	 * Constructs a ConsoleAreaOutputStream that redirects output to the specified ConsoleArea.
	 *
	 * @param consoleArea the ConsoleArea instance to which the output will be redirected
	 */
	public ConsoleAreaOutputStream(ConsoleArea consoleArea) {
		this.consoleArea = consoleArea;
	}
	
	
	// Methods:
	/**
	 * Flushes the current content of the StringBuilder to the associated ConsoleArea.
	 * This method clears the StringBuilder after writing its content to ensure no residual data is retained.
	 * It uses the `outPrint` method of the ConsoleArea to display the message in the console view.
	 */
	@Override
	public void flush() {
		consoleArea.outPrint(stringBuilder.toString());
		stringBuilder.setLength(0);
	}
	
	/**
	 * Closes this stream and releases any system resources associated with it.
	 * <p>
	 * This method attempts to close the underlying stream by invoking the `close`
	 * method of the parent class. If an IOException occurs during the close
	 * attempt, the error is logged using `System.err` with the exception details.
	 * </p>
	 * <p>
	 * The implementation ensures that the `close` operation of the parent
	 * class is safely invoked, even if an error occurs, providing proper error
	 * handling and feedback.
	 * </p>
	 */
	@Override
	public void close() {
		try {
			super.close();
		} catch (IOException e) {
			System.err.println("Failed to close ConsoleAreaOutputStream: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Writes a single byte to the output stream. This method processes the given byte and decides how to handle
	 * it based on its value:
	 * <p>- If the byte represents a newline character (`\n`), it triggers the handling of a new line.</p>
	 * <p>- If the byte represents a carriage return (`\r`), it ignores it.</p>
	 * <p>- For other bytes, it appends the character representation of the byte to the internal buffer and prints
	 * it to the associated ConsoleArea.</p>
	 *
	 * @param b the byte to be written, typically representing a character value or control character
	 */
	@Override
	public void write(int b) {
		if (b == '\n') {
			handleNewLineCharacter();
			return;
		}
		if (b == '\r') {
			return;
		}
		appendAndPrint(b);
	}
	
	/**
	 * Writes a portion of a byte array to the associated ConsoleArea.
	 * This method converts the specified portion of the byte array into a string
	 * and uses the outPrint method of the ConsoleArea to display it.
	 *
	 * @param b   the byte array containing the data to be written
	 * @param off the start offset in the array where writing begins
	 * @param len the number of bytes to write starting from the offset
	 */
	@Override
	public void write(byte[] b, int off, int len) {
		consoleArea.outPrint(new String(b, off, len));
	}
	
	/**
	 * Handles the occurrence of a newline character within the output process.
	 * <p>
	 * This method processes the current content of the internal `StringBuilder` by
	 * appending a newline character to the accumulated text and printing the result
	 * to the associated `ConsoleArea` using its `outPrint` method. Afterward, the
	 * `StringBuilder` is cleared to ensure that no residual data remains for future use.
	 * </p>
	 */
	private void handleNewLineCharacter() {
		consoleArea.outPrint(stringBuilder.toString() + "\n"); //TODO safely remove toString()?
		stringBuilder.setLength(0);
	}
	
	/**
	 * Appends the character representation of the given byte to the internal StringBuilder and prints it
	 * to the associated ConsoleArea. This method ensures that the character is both added to an internal
	 * buffer and displayed in the console output.
	 *
	 * @param b the byte to be converted to a character and processed
	 */
	private void appendAndPrint(int b) {
		stringBuilder.append((char)b);
		consoleArea.outPrint(Character.toString((char)b));
	}
}