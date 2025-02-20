package main.java.zenit;

import java.io.IOException;
import java.io.OutputStream;
import javafx.scene.control.TextArea;

/**
 * A custom {@link OutputStream} that redirects output to a {@link TextArea} in a JavaFX application.
 * This allows console output to be displayed in a GUI component instead of the system console.
 */
public class TextAreaOutputStream extends OutputStream {
	private TextArea textArea;
	private StringBuilder stringBuilder = new StringBuilder();
	
	/**
	 * Constructs a new {@code TextAreaOutputStream} that writes output to the specified {@code TextArea}.
	 *
	 * @param textArea the JavaFX {@code TextArea} where output will be displayed
	 */
	public TextAreaOutputStream(TextArea textArea) {
		this.textArea = textArea;
	}
	
	/**
	 * Flushes the buffered output to the {@code TextArea}.
	 * Appends the contents of the buffer to the text area and then clears the buffer.
	 */
	@Override
	public void flush() {
		textArea.appendText(stringBuilder.toString());
		stringBuilder.setLength(0);
	}
	
	/**
	 * Closes the stream. Calls {@code super.close()} and catches any {@link IOException}.
	 */
	@Override
	public void close() {
		try {
			super.close();
		} catch (IOException e) {
			System.out.println("Error in TextAreaOutputStream close() = " + e);
		}
	}
	
	/**
	 * Writes a single byte to the {@code TextArea}.
	 * If the byte represents a newline character, the buffer is flushed.
	 * Carriage return characters are ignored.
	 *
	 * @param inputByte the byte to be written
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	public void write(int inputByte) throws IOException {
		switch (inputByte) {
			case '\n':
				textArea.appendText(stringBuilder.toString() + "\n");
				stringBuilder.setLength(0);
				return;
			case '\r':
				return;
			default:
				stringBuilder.append((char) inputByte);
				textArea.appendText(Character.toString((char) inputByte));
		}
	}
	
	/**
	 * Writes a sequence of bytes to the {@code TextArea}.
	 * Converts the byte array to a string and appends it to the text area.
	 *
	 * @param bytes  the byte array containing the data to write
	 * @param offset the starting position in the array
	 * @param length the number of bytes to write
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	public void write(byte bytes[], int offset, int length) throws IOException {
		textArea.appendText(new String(bytes, offset, length));
	}
}
