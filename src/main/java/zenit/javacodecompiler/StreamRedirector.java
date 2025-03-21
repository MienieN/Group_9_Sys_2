package main.java.zenit.javacodecompiler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

/**
 * A class that redirects the lines of an InputStream to a Consumer.
 * This class implements Runnable and can be used to process input streams
 * in a separate thread.
 */
public class StreamRedirector implements Runnable {

    private InputStream inputStream;
    private Consumer<String> consumer;

    /**
     * Constructs a StreamRedirector that processes lines from the provided InputStream
     * and sends them to the provided Consumer.
     *
     * @param inputStream The input stream to read from.
     * @param consumer    The consumer that processes each line.
     */
    public StreamRedirector(InputStream inputStream, Consumer<String> consumer) {
        this.inputStream = inputStream;
        this.consumer = consumer;
    }

    /**
     * Reads the InputStream line by line and sends each line to the Consumer.
     * This method runs in a separate thread and processes the input stream as it is received.
     */
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            reader.lines().forEach(consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
