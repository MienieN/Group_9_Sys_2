package main.java.zenit.javacodecompiler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class StreamRedirector implements Runnable {

	private InputStream inputStream;
	private Consumer<String> consumer;
	
	public StreamRedirector(InputStream inputStream, Consumer<String> consumer) {
		this.inputStream = inputStream;
		this.consumer = consumer;
	}

	public void run() {
		new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
	}
}
