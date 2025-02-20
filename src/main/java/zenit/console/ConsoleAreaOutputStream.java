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

	
	@Override
	public void flush() {
		consoleArea.outPrint(stringBuilder.toString());
		stringBuilder.setLength(0);
	}

	
	@Override
	public void close() {
		try {
			super.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}

	@Override
	public void write(int b) {
		if (b == '\n') {
			consoleArea.outPrint(stringBuilder.toString() + "\n");
			stringBuilder.setLength(0);
			return;
		}
		if (b == '\r') {
			return;
		}
		stringBuilder.append((char)b);
		consoleArea.outPrint(Character.toString((char)b));
	}

	@Override
	public void write(byte[] b, int off, int len) {
		 consoleArea.outPrint(new String(b, off, len));
	}
}